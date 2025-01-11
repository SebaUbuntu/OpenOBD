/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Socket
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.obd.commands.Command
import dev.sebaubuntu.openobd.obd.commands.elm327.ResetCommand
import dev.sebaubuntu.openobd.obd.commands.elm327.SetEchoCommand
import dev.sebaubuntu.openobd.obd.commands.elm327.SetObdProtocolCommand
import dev.sebaubuntu.openobd.obd.commands.elm327.ShowHeadersCommand
import dev.sebaubuntu.openobd.obd.models.ObdProtocol
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

/**
 * ELM327 IC manager.
 *
 * @param clock The clock to use for timing
 */
class Elm327Manager(
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val clock: Clock = Clock.System,
) {
    private val socket = MutableStateFlow<Socket?>(null)

    /**
     * Mutex to allow sequential execution of commands.
     */
    private val mutex = Mutex()

    /**
     * Response buffer.
     *
     * Protected by [mutex].
     */
    private val responseBufferByteArray = ByteArray(BUFFER_SIZE)

    /**
     * Update the current socket and initialize it if not null.
     *
     * @param socket The new socket to use
     */
    suspend fun setSocket(socket: Socket?) = withContext(dispatcher) {
        // Cancel all pending commands
        this@Elm327Manager.socket.value = null

        // Initialize the connection
        socket?.let {
            // Reset the ELM327
            it.executeCommand(ResetCommand)

            // Set echo off
            it.executeCommand(SetEchoCommand(false))

            // Set automatic protocol
            it.executeCommand(SetObdProtocolCommand(ObdProtocol.AUTOMATIC))

            // Show headers to identify the ECU
            it.executeCommand(ShowHeadersCommand(true))
        }

        this@Elm327Manager.socket.value = socket
    }

    /**
     * Execute an OBD command.
     *
     * @param command The OBD command to execute
     * @param timeoutMs The timeout in milliseconds for each sub-operation, not for the whole
     *   execution
     */
    suspend fun <T> executeCommand(
        command: Command<T>,
        timeoutMs: Long = DEFAULT_COMMAND_TIMEOUT_MS,
    ) = withContext(dispatcher) {
        socket.value?.executeCommand(
            command = command,
            timeoutMs = timeoutMs,
        ) ?: Result.Error(Error.IO)
    }

    /**
     * Get a flow that polls a command.
     *
     * @param command The command to poll
     * @param pollIntervalMs The interval in milliseconds between each poll
     * @param timeoutMs The timeout in milliseconds for each sub-operation, not for the whole
     *   execution
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> pollCommand(
        command: Command<T>,
        pollIntervalMs: Long,
        timeoutMs: Long = DEFAULT_COMMAND_TIMEOUT_MS,
    ) = socket
        .flatMapLatest { socket ->
            socket?.let {
                channelFlow {
                    while (true) {
                        val startMs = clock.now().toEpochMilliseconds()

                        val result = it.executeCommand(
                            command = command,
                            timeoutMs = timeoutMs,
                        )

                        val endMs = clock.now().toEpochMilliseconds()

                        send(result)

                        val remainingTime = pollIntervalMs - (endMs - startMs)
                        if (remainingTime > 0) {
                            delay(remainingTime)
                        }
                    }
                }
            } ?: flowOf<Result<T, Error>>(Result.Error(Error.IO))
        }
        .flowOn(dispatcher)

    /**
     * Execute a command on the socket.
     */
    private suspend fun <T> Socket.executeCommand(
        command: Command<T>,
        timeoutMs: Long = DEFAULT_COMMAND_TIMEOUT_MS,
    ): Result<T, Error> {
        var response = ""

        mutex.withLock {
            // Flush the input stream
            inputStream.flush()

            // Write the command
            outputStream.write("${command.command}\r".encodeToByteArray())
            outputStream.flush()

            // Now wait for the response
            val start = clock.now().toEpochMilliseconds()
            while (true) {
                // Handle timeout
                val now = clock.now().toEpochMilliseconds()
                if (now - start > timeoutMs) {
                    Logger.warn(LOG_TAG) { "Timeout: ${now - start} > $timeoutMs" }
                    return Result.Error(Error.CANCELLED)
                }

                // Read data
                val bytes = inputStream.available()
                if (bytes > 0) {
                    val readBytes = inputStream.read(responseBufferByteArray)
                    response += responseBufferByteArray.decodeToString(endIndex = readBytes)
                    if (response.contains(ENDING_CHAR)) {
                        break
                    }
                }

                delay(50)
            }
        }

        return parseResponse(command, response)
    }

    /**
     * Parse the response.
     *
     * @param command The original command of the response
     * @param response The response to parse
     */
    private fun <T> parseResponse(
        command: Command<T>,
        response: String
    ): Result<T, Error> {
        if (!response.contains(ENDING_CHAR)) {
            Logger.error(LOG_TAG) { "No ending character found: $response" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        return command.parseResponse(
            /**
             * - Remove bogus null chars per ELM327 documentation
             * - Limit the response to the ending character
             * - Trim the response (usually contains multiple carriage return characters)
             */
            response
                .replace("\u0000", "")
                .substringBefore(ENDING_CHAR)
                .trim()
        )
    }

    companion object {
        private val LOG_TAG = Elm327Manager::class.simpleName!!

        /**
         * Timeout value if not specified.
         */
        private const val DEFAULT_COMMAND_TIMEOUT_MS = 15000L

        /**
         * Buffer size for the input stream.
         */
        private const val BUFFER_SIZE = 1024

        /**
         * Character used to end the response. It indicates ready for next command.
         */
        private const val ENDING_CHAR = '>'
    }
}
