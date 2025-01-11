/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.elm327

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Result.Companion.flatMap
import dev.sebaubuntu.openobd.core.models.Socket
import dev.sebaubuntu.openobd.elm327.commands.ResetCommand
import dev.sebaubuntu.openobd.elm327.commands.SetEchoCommand
import dev.sebaubuntu.openobd.elm327.commands.SetObdProtocolCommand
import dev.sebaubuntu.openobd.elm327.commands.ShowHeadersCommand
import dev.sebaubuntu.openobd.elm327.models.ObdProtocol
import dev.sebaubuntu.openobd.logging.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.io.Buffer
import kotlinx.io.InternalIoApi
import kotlinx.io.readString
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * ELM327 IC manager.
 *
 * @param socket The [Socket] to use for communication
 * @param coroutineDispatcher The [CoroutineDispatcher] to use for coroutines
 * @param clock The clock to use for timing
 */
@OptIn(ExperimentalTime::class)
class Elm327Session(
    private val socket: Socket,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val clock: Clock = Clock.System,
) {
    /**
     * Mutex to allow sequential execution of commands.
     */
    private val mutex = Mutex()

    /**
     * Response buffer.
     *
     * Protected by [mutex].
     */
    private val responseBuffer = Buffer()

    /**
     * Initialize/reset the ELM327.
     */
    suspend fun init() = withContext(coroutineDispatcher) {
        // Reset the ELM327
        socket.executeCommand(ResetCommand)

        // Set echo off
        socket.executeCommand(SetEchoCommand(false))

        // Set automatic protocol
        socket.executeCommand(SetObdProtocolCommand(ObdProtocol.AUTOMATIC))

        // Show headers to identify the ECU
        socket.executeCommand(ShowHeadersCommand(true))
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
    ) = withContext(coroutineDispatcher) {
        socket.executeCommand(
            command = command,
            timeoutMs = timeoutMs,
        )
    }

    /**
     * Get a flow that polls a command.
     *
     * @param command The command to poll
     * @param pollIntervalMs The interval in milliseconds between each poll, null to disable polling
     * @param timeoutMs The timeout in milliseconds for each sub-operation, not for the whole
     *   execution
     */
    fun <T> pollCommand(
        command: Command<T>,
        pollIntervalMs: Long?,
        timeoutMs: Long = DEFAULT_COMMAND_TIMEOUT_MS,
    ) = channelFlow {
        pollIntervalMs?.also { pollIntervalMs ->
            while (true) {
                val startMs = clock.now().toEpochMilliseconds()

                val result = socket.executeCommand(
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
        } ?: run {
            val result = socket.executeCommand(
                command = command,
                timeoutMs = timeoutMs,
            )

            send(result)
        }
    }.flowOn(coroutineDispatcher)

    /**
     * Execute a command on the socket.
     */
    private suspend fun <T> Socket.executeCommand(
        command: Command<T>,
        timeoutMs: Long = DEFAULT_COMMAND_TIMEOUT_MS,
    ): Result<T, Error> {
        val response = executeCommandRaw(
            command = command.command,
            timeoutMs = timeoutMs,
        )

        return response.flatMap {
            command.parseResponse(it)
        }
    }

    /**
     * Execute the command and return the response split by carriage return.
     * The response will be stripped out of the idle character.
     * ELM327 specific messages will also be removed from the response (and treated as error in case
     * it indicates one).
     */
    private suspend fun Socket.executeCommandRaw(
        command: String,
        timeoutMs: Long = DEFAULT_COMMAND_TIMEOUT_MS,
    ): Result<List<String>, Error> = mutex.withLock {
        val response = mutableListOf<String>()
        var currentResponse = ""

        // Drain the source buffer
        @OptIn(InternalIoApi::class)
        source.buffer.readAtMostTo(Buffer(), Long.MAX_VALUE)

        // Write the command
        sink.write("${command}\r".encodeToByteArray())
        sink.flush()

        // Read the response
        var avoidTimeout = false
        while (true) {
            // Read data
            responseBuffer.clear()
            val readBytes = when (avoidTimeout) {
                true -> {
                    avoidTimeout = false
                    Logger.info(LOG_TAG) { "Reading response w/o timeout" }
                    source.readAtMostTo(responseBuffer, Long.MAX_VALUE)
                }

                false -> withTimeoutOrNull(timeoutMs) {
                    source.readAtMostTo(responseBuffer, Long.MAX_VALUE)
                } ?: run {
                    Logger.error(LOG_TAG) { "Timed out waiting for response" }
                    return Result.Error(Error.IO)
                }
            }

            if (readBytes < 0) {
                Logger.error(LOG_TAG) { "Source exhausted" }
                return Result.Error(Error.IO)
            }

            currentResponse += responseBuffer.readString(readBytes)

            val lines = currentResponse
                .replace("\u0000", "")
                .split(multipleCarriageReturnRegex)

            var foundEndingChar = false
            lines.dropLast(1).forEach { line ->
                when (val cleanedUpLine = line.trim()) {
                    "?" -> {
                        Logger.error(LOG_TAG) { "Unknown command: \"${command}\"" }
                        return Result.Error(Error.NOT_IMPLEMENTED)
                    }

                    IDLE_MESSAGE -> {
                        foundEndingChar = true
                    }

                    "NO DATA" -> {
                        // Skip, do nothing
                    }

                    "SEARCHING..." -> {
                        Logger.info(LOG_TAG) { "Searching for ECUs, avoiding timeout" }
                        avoidTimeout = true
                    }

                    "BUFFER FULL" -> {
                        Logger.error(LOG_TAG) { "ELM327 buffer is full" }
                        return Result.Error(Error.IO)
                    }

                    "BUS BUSY" -> {
                        Logger.error(LOG_TAG) { "Bus is busy" }
                        return Result.Error(Error.IO)
                    }

                    "BUS ERROR" -> {
                        Logger.error(LOG_TAG) { "Bus error" }
                        return Result.Error(Error.IO)
                    }

                    "CAN ERROR" -> {
                        Logger.error(LOG_TAG) { "CAN error" }
                        return Result.Error(Error.IO)
                    }

                    "DATA ERROR" -> {
                        Logger.error(LOG_TAG) { "Data error" }
                        return Result.Error(Error.INVALID_RESPONSE)
                    }

                    "FB ERROR" -> {
                        Logger.error(LOG_TAG) { "Feedback error" }
                        return Result.Error(Error.IO)
                    }

                    "LP ALERT" -> {
                        Logger.error(LOG_TAG) { "Low power alert" }
                        return Result.Error(Error.IO)
                    }

                    "LV RESET" -> {
                        Logger.error(LOG_TAG) { "Low voltage reset" }
                        return Result.Error(Error.IO)
                    }

                    "STOPPED" -> {
                        Logger.error(LOG_TAG) { "ELM327 stopped" }
                        return Result.Error(Error.IO)
                    }

                    "UNABLE TO CONNECT" -> {
                        Logger.error(LOG_TAG) { "Unable to connect" }
                        return Result.Error(Error.IO)
                    }

                    "BUS INIT... ERROR" -> {
                        Logger.error(LOG_TAG) { "Bus initialization error" }
                        return Result.Error(Error.IO)
                    }

                    else -> {
                        response.add(cleanedUpLine)
                    }
                }
            }

            if (lines.lastOrNull()?.trim() == IDLE_MESSAGE) {
                foundEndingChar = true
            }

            currentResponse = lines.lastOrNull()?.takeUnless { foundEndingChar } ?: ""

            if (foundEndingChar) {
                break
            }
        }

        if (currentResponse.isNotBlank()) {
            Logger.warn(LOG_TAG) {
                "Current response is not empty: $currentResponse, yet ending character was found"
            }
        }

        return Result.Success(response)
    }

    companion object {
        private val LOG_TAG = Elm327Session::class.simpleName!!

        /**
         * Timeout value if not specified.
         */
        private const val DEFAULT_COMMAND_TIMEOUT_MS = 5000L

        /**
         * Character used to end the response. It indicates ready for next command.
         */
        private const val IDLE_MESSAGE = ">"

        /**
         * Regex to match multiple carriage return characters.
         */
        private val multipleCarriageReturnRegex = "\\r+".toRegex()
    }
}
