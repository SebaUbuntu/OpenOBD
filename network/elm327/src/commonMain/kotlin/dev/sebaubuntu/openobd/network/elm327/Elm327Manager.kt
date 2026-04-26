/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.elm327

import dev.sebaubuntu.openobd.backend.models.RawSocket
import dev.sebaubuntu.openobd.backend.models.Socket
import dev.sebaubuntu.openobd.backend.models.Socket.Companion.buffered
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Result.Companion.flatMap
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.network.can.CanFrame
import dev.sebaubuntu.openobd.network.core.Transceiver
import dev.sebaubuntu.openobd.network.elm327.commands.RawCanCommand
import dev.sebaubuntu.openobd.network.elm327.commands.ResetCommand
import dev.sebaubuntu.openobd.network.elm327.commands.SetEchoCommand
import dev.sebaubuntu.openobd.network.elm327.commands.SetObdProtocolCommand
import dev.sebaubuntu.openobd.network.elm327.commands.ShowDataLengthCodeCommand
import dev.sebaubuntu.openobd.network.elm327.commands.ShowHeadersCommand
import dev.sebaubuntu.openobd.network.elm327.models.Elm327Message
import dev.sebaubuntu.openobd.network.elm327.models.ObdProtocol
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
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
import kotlinx.io.indexOf
import kotlinx.io.readString
import kotlinx.io.writeString
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

                // Hide the CAN DLC value
                it.executeCommand(ShowDataLengthCodeCommand(false))
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

            is Result.Failure -> {
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
        ) ?: Result.Failure(Error.IO)
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
    ) = coroutineScope.async(coroutineDispatcher) {
        mutex.withLock {
            val response = mutableListOf<String>()

            // Write the command
            runCatching {
                sink.writeString("${command.command}\r")
                sink.flush()
            }.onFailure {
                Logger.error(LOG_TAG, it) { "Failed to write command" }
                return@withLock Result.Failure(Error.IO)
            }

            // Read the response
            var avoidTimeout = false
            while (true) {
                val responseBuffer = Buffer()

                val lineBreakIndex = runCatching {
                    when (avoidTimeout) {
                        true -> {
                            avoidTimeout = false
                            Logger.info(LOG_TAG) { "Reading response w/o timeout" }
                            source.indexOf(IDLE_MESSAGE)
                        }

                        false -> withTimeoutOrNull(timeout = timeout) {
                            source.indexOf(IDLE_MESSAGE)
                        } ?: run {
                            Logger.error(LOG_TAG) { "Timed out waiting for response" }
                            return@withLock Result.Failure(Error.IO)
                        }
                    }
                }.getOrElse {
                    Logger.error(LOG_TAG, it) { "Failed to read response" }
                    return@withLock Result.Failure(Error.IO)
                }

                when (lineBreakIndex) {
                    -1L -> break

                    else -> {
                        // Read everything before the line break
                        source.readTo(responseBuffer, lineBreakIndex)

                        // Then discard the line break itself
                        source.skip(1)
                    }
                }

                val rawResponse = responseBuffer.readString().replace("\u0000", "")

                for (line in rawResponse.split(LINE_DELIMITER)) {
                    val cleanedUpLine = line.trim()

                    if (cleanedUpLine.isBlank()) {
                        continue
                    }

                    when (val message = Elm327Message.from(cleanedUpLine)) {
                        is Elm327Message.Alert -> when (message) {
                            is Elm327Message.Alert.SearchingEcus -> {
                                Logger.warn(LOG_TAG) { "Searching for ECUs, avoiding timeout" }
                                avoidTimeout = true
                            }

                            else -> Logger.warn(LOG_TAG) { "Received alert: $message, ignoring" }
                        }

                        is Elm327Message.Error -> {
                            Logger.error(LOG_TAG) { "Received error: $message" }
                            return@withLock Result.Failure(Error.INVALID_RESPONSE)
                        }

                        else -> response.add(cleanedUpLine)
                    }
                }

                break
            }

            Result.Success(response)
        }
    }.await().flatMap(command::parseResponse)

    companion object {
        private val LOG_TAG = Elm327Manager::class.simpleName!!

        /**
         * Timeout value if not specified.
         */
        private val DEFAULT_COMMAND_TIMEOUT = 5000.milliseconds

        /**
         * Character used to end the response. It indicates ready for next command.
         */
        private const val IDLE_MESSAGE = '>'.code.toByte()

        /**
         * Regex to match multiple carriage return characters.
         */
        private const val LINE_DELIMITER = '\r'
    }
}
