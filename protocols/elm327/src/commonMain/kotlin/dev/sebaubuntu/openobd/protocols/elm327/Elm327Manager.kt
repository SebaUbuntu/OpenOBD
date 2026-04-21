/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.elm327

import dev.sebaubuntu.openobd.backend.models.RawSocket
import dev.sebaubuntu.openobd.backend.models.Socket
import dev.sebaubuntu.openobd.backend.models.Socket.Companion.buffered
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Result.Companion.flatMap
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.protocols.can.CanFrame
import dev.sebaubuntu.openobd.protocols.core.Transceiver
import dev.sebaubuntu.openobd.protocols.elm327.commands.RawCanCommand
import dev.sebaubuntu.openobd.protocols.elm327.commands.ResetCommand
import dev.sebaubuntu.openobd.protocols.elm327.commands.SetEchoCommand
import dev.sebaubuntu.openobd.protocols.elm327.commands.SetObdProtocolCommand
import dev.sebaubuntu.openobd.protocols.elm327.commands.ShowDataLengthCodeCommand
import dev.sebaubuntu.openobd.protocols.elm327.commands.ShowHeadersCommand
import dev.sebaubuntu.openobd.protocols.elm327.models.ObdProtocol
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.io.Buffer
import kotlinx.io.InternalIoApi
import kotlinx.io.readString
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource
import kotlin.time.measureTimedValue

/**
 * ELM327 IC manager.
 *
 * @param coroutineScope The [CoroutineScope] to use for coroutines
 * @param coroutineDispatcher The [CoroutineDispatcher] to use for coroutines
 */
class Elm327Manager(
    private val coroutineScope: CoroutineScope,
    private val coroutineDispatcher: CoroutineDispatcher,
) : Transceiver<CanFrame> {
    enum class Status {
        /**
         * No socket is connected.
         */
        IDLE,

        /**
         * The socket is connected and the ELM327 is initializing.
         */
        INITIALIZING,

        /**
         * The socket is connected and the ELM327 is ready to use.
         */
        READY,
    }

    /**
     * Requested socket holder.
     */
    private val rawSocket = MutableStateFlow<RawSocket?>(null)

    /**
     * The initialized socket.
     */
    private val socket = rawSocket
        .map { rawSocket ->
            rawSocket?.buffered()?.also {
                // Reset the ELM327
                it.executeCommand(ResetCommand)

                // Set echo off
                it.executeCommand(SetEchoCommand(false))

                // Set automatic protocol
                it.executeCommand(SetObdProtocolCommand(ObdProtocol.AUTOMATIC))

                // Show headers to identify the ECU
                it.executeCommand(ShowHeadersCommand(true))

                // Show the DLC value
                it.executeCommand(ShowDataLengthCodeCommand(true))
            }
        }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = null,
        )

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

    val status = combine(
        rawSocket,
        socket,
    ) { requestedSocket, socket ->
        when {
            requestedSocket == null -> Status.IDLE
            socket == null -> Status.INITIALIZING
            else -> Status.READY
        }
    }

    /**
     * CAN messages flow.
     */
    private val canFramesFlow = MutableSharedFlow<CanFrame>()

    override fun receive() = canFramesFlow.asSharedFlow()

    @OptIn(ExperimentalUnsignedTypes::class)
    override suspend fun transmit(frame: CanFrame) {
        val dataFrame = frame as? CanFrame.Classic.Data ?: error("Unsupported frame type")

        val command = RawCanCommand(dataFrame.data)

        when (val result = executeCommand(command)) {
            is Result.Success -> result.data.value.forEach { (canIdentifier, data) ->
                val canFrame = CanFrame.Classic.Data(
                    identifier = canIdentifier,
                    data = data.toList()
                )

                canFramesFlow.emit(canFrame)
            }

            is Result.Error -> {
                Logger.error(LOG_TAG, result.throwable) { "Failed to transmit CAN frame" }
                return
            }
        }
    }

    /**
     * Set the socket. Set to null to disconnect.
     */
    fun setSocket(socket: RawSocket?) {
        rawSocket.value = socket
    }

    /**
     * Execute a command.
     *
     * @param command The [Command] to execute
     * @param timeout The timeout in milliseconds for each sub-operation, not for the whole
     *   execution
     */
    suspend fun <T> executeCommand(
        command: Command<T>,
        timeout: Duration = DEFAULT_COMMAND_TIMEOUT,
    ) = withContext(coroutineDispatcher) {
        socket.value?.executeCommand(
            command = command,
            timeout = timeout,
        ) ?: Result.Error(Error.IO)
    }

    /**
     * Get a flow that polls a command.
     *
     * @param command The [Command] to poll
     * @param pollInterval The interval in milliseconds between each poll, null to disable polling
     * @param timeout The timeout for each sub-operation, not for the whole execution
     */
    fun <T> pollCommand(
        command: Command<T>,
        pollInterval: Duration?,
        timeout: Duration = DEFAULT_COMMAND_TIMEOUT,
    ) = channelFlow {
        pollInterval?.also { pollInterval ->
            while (true) {
                val (result, duration) = TimeSource.Monotonic.measureTimedValue {
                    executeCommand(
                        command = command,
                        timeout = timeout,
                    )
                }

                send(result)

                delay(pollInterval - duration)
            }
        } ?: run {
            val result = executeCommand(
                command = command,
                timeout = timeout,
            )

            send(result)
        }
    }.flowOn(coroutineDispatcher)

    /**
     * Execute a command on the socket.
     */
    private suspend fun <T> Socket.executeCommand(
        command: Command<T>,
        timeout: Duration = DEFAULT_COMMAND_TIMEOUT,
    ) = withContext(coroutineDispatcher) {
        mutex.withLock {
            val response = mutableListOf<String>()

            // Drain the source buffer
            runCatching {
                @OptIn(InternalIoApi::class)
                source.buffer.readAtMostTo(Buffer(), Long.MAX_VALUE)
            }.onFailure {
                Logger.error(LOG_TAG, it) { "Failed to drain source buffer" }
                return@withContext Result.Error(Error.IO)
            }

            // Write the command
            runCatching {
                sink.write("${command.command}\r".encodeToByteArray())
                sink.flush()
            }.onFailure {
                Logger.error(LOG_TAG, it) { "Failed to write command" }
                return@withContext Result.Error(Error.IO)
            }

            // Read the response
            var partialResponse = ""
            var avoidTimeout = false
            while (true) {
                // Read data
                responseBuffer.clear()
                val bytesRead = runCatching {
                    when (avoidTimeout) {
                        true -> {
                            avoidTimeout = false
                            Logger.info(LOG_TAG) { "Reading response w/o timeout" }
                            source.readAtMostTo(responseBuffer, Long.MAX_VALUE)
                        }

                        false -> withTimeoutOrNull(timeout = timeout) {
                            source.readAtMostTo(responseBuffer, Long.MAX_VALUE)
                        } ?: run {
                            Logger.error(LOG_TAG) { "Timed out waiting for response" }
                            return@withContext Result.Error(Error.IO)
                        }
                    }
                }.getOrElse {
                    Logger.error(LOG_TAG, it) { "Failed to read response" }
                    return@withContext Result.Error(Error.IO)
                }

                if (bytesRead < 0) {
                    Logger.error(LOG_TAG) { "Source exhausted" }
                    return@withContext Result.Error(Error.IO)
                }

                partialResponse += responseBuffer.readString(bytesRead).replace("\u0000", "")

                val (lines, remainder) = partialResponse.splitWithRemainder(LINE_DELIMITER)
                partialResponse = remainder ?: ""

                var foundIdleMessage = false
                for (line in lines) {
                    when (val cleanedUpLine = line.trim()) {
                        "?" -> {
                            Logger.error(LOG_TAG) { "Unknown command: \"${command.command}\"" }
                            return@withContext Result.Error(Error.NOT_IMPLEMENTED)
                        }

                        IDLE_MESSAGE -> {
                            foundIdleMessage = true
                            break
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
                            return@withContext Result.Error(Error.IO)
                        }

                        "BUS BUSY" -> {
                            Logger.error(LOG_TAG) { "Bus is busy" }
                            return@withContext Result.Error(Error.IO)
                        }

                        "BUS ERROR" -> {
                            Logger.error(LOG_TAG) { "Bus error" }
                            return@withContext Result.Error(Error.IO)
                        }

                        "CAN ERROR" -> {
                            Logger.error(LOG_TAG) { "CAN error" }
                            return@withContext Result.Error(Error.IO)
                        }

                        "DATA ERROR" -> {
                            Logger.error(LOG_TAG) { "Data error" }
                            return@withContext Result.Error(Error.INVALID_RESPONSE)
                        }

                        "FB ERROR" -> {
                            Logger.error(LOG_TAG) { "Feedback error" }
                            return@withContext Result.Error(Error.IO)
                        }

                        "LP ALERT" -> {
                            Logger.error(LOG_TAG) { "Low power alert" }
                            return@withContext Result.Error(Error.IO)
                        }

                        "LV RESET" -> {
                            Logger.error(LOG_TAG) { "Low voltage reset" }
                            return@withContext Result.Error(Error.IO)
                        }

                        "STOPPED" -> {
                            Logger.error(LOG_TAG) { "ELM327 stopped" }
                            return@withContext Result.Error(Error.IO)
                        }

                        "UNABLE TO CONNECT" -> {
                            Logger.error(LOG_TAG) { "Unable to connect" }
                            return@withContext Result.Error(Error.IO)
                        }

                        "BUS INIT... ERROR" -> {
                            Logger.error(LOG_TAG) { "Bus initialization error" }
                            return@withContext Result.Error(Error.IO)
                        }

                        else -> {
                            response.add(cleanedUpLine)
                        }
                    }
                }

                if (remainder?.trim() == IDLE_MESSAGE) {
                    foundIdleMessage = true
                }

                if (foundIdleMessage) {
                    break
                }
            }

            partialResponse = partialResponse.trim()
            if (partialResponse != IDLE_MESSAGE) {
                Logger.warn(LOG_TAG) { "Partial response not empty: $partialResponse" }
            }

            Result.Success<_, Error>(response)
        }
    }.flatMap(command::parseResponse)

    private fun String.splitWithRemainder(delimiter: Char): Pair<List<String>, String?> {
        val substrings = mutableListOf<String>()
        var startIndex = 0

        // Skip any initial consecutive delimiters
        while (startIndex < length && this[startIndex] == delimiter) {
            startIndex++
        }

        while (startIndex < length) {
            val delimiterIndex = indexOf(delimiter, startIndex)
            if (delimiterIndex == -1) {
                // No more delimiters found, the rest is the remainder
                break
            }

            // Extract the substring up to (but not including) the delimiter
            val substring = substring(startIndex, delimiterIndex)

            // Add the substring only if it's not empty
            if (substring.isNotEmpty()) {
                substrings.add(substring)
            }

            // Find the next start index by skipping consecutive delimiters
            var nextStartIndex = delimiterIndex + 1
            while (nextStartIndex < length && this[nextStartIndex] == delimiter) {
                nextStartIndex++
            }
            startIndex = nextStartIndex
        }

        return substrings to when (startIndex < length) {
            true -> substring(startIndex)
            else -> null
        }
    }

    companion object {
        private val LOG_TAG = Elm327Manager::class.simpleName!!

        /**
         * Timeout value if not specified.
         */
        private val DEFAULT_COMMAND_TIMEOUT = 5000.milliseconds

        /**
         * Character used to end the response. It indicates ready for next command.
         */
        private const val IDLE_MESSAGE = ">"

        /**
         * Regex to match multiple carriage return characters.
         */
        private const val LINE_DELIMITER = '\r'
    }
}
