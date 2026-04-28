/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.devices.demo

import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.network.core.RawSocket
import io.ktor.util.toUpperCasePreservingASCIIRules
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import kotlinx.io.indexOf
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlin.random.Random
import kotlin.random.nextUBytes

/**
 * ELM327 emulator.
 */
@OptIn(ExperimentalUnsignedTypes::class)
class Elm327Emulator(
    coroutineScope: CoroutineScope,
) : RawSocket {
    /**
     * IC internal state.
     */
    data class InternalState(
        // ELM327
        val echoEnabled: Boolean,
        val headersEnabled: Boolean,
        val displayCanDataLengthCode: Boolean,

        // OBD2
        val storedDiagnosticTroubleCodes: List<UShort>,
    ) {
        companion object {
            val DEFAULT = InternalState(
                echoEnabled = false,
                headersEnabled = false,
                displayCanDataLengthCode = false,
                storedDiagnosticTroubleCodes = listOf(
                    0x0470u,
                    0x0471u,
                ),
            )
        }
    }

    private var internalState = InternalState.DEFAULT

    private val receiveBuffer = Buffer()
    private val transferBuffer = Buffer()

    private val receiveChannel = Channel<Unit>(1)
    private val transferChannel = Channel<Unit>(1)

    private val atResponses = mapOf<String, suspend () -> List<String>>(
        "@1" to {
            listOf("OBDII to RS232 Interpreter")
        },
        "D0" to {
            internalState = internalState.copy(displayCanDataLengthCode = false)
            listOf("OK")
        },
        "D1" to {
            internalState = internalState.copy(displayCanDataLengthCode = true)
            listOf("OK")
        },
        "DP" to {
            listOf("AUTO, ISO 15765-4 (CAN 11/500)")
        },
        "DPN" to {
            listOf("A6")
        },
        "E0" to {
            internalState = internalState.copy(echoEnabled = false)
            listOf("OK")
        },
        "E1" to {
            internalState = internalState.copy(echoEnabled = true)
            listOf("OK")
        },
        "H0" to {
            internalState = internalState.copy(headersEnabled = false)
            listOf("OK")
        },
        "H1" to {
            internalState = internalState.copy(headersEnabled = true)
            listOf("OK")
        },
        "I" to {
            listOf(ELM327_VERSION)
        },
        "IGN" to {
            listOf(ignMonValues.random())
        },
        "RV" to {
            listOf(voltageValues.random())
        },
        "SP0" to {
            listOf("OK")
        },
        "Z" to {
            coroutineScope {
                // Simulate restart
                val delayJob = launch {
                    delay(1000L)
                }

                // Reset internal state
                internalState = InternalState.DEFAULT

                // Wait for the fake delay
                delayJob.join()

                listOf(ELM327_VERSION)
            }
        },
    )

    private val canResponses = mapOf(
        // Service 01
        obd2Response(0x01u, 0x00u) { ubyteArrayOf(0xBFu, 0xFFu, 0xFFu, 0xFFu) },
        randomObd2Response(0x01u, 0x01u, responseSize = 4),
        obd2Response(0x01u, 0x03u) { ubyteArrayOf(0x02u, 0x00u) },
        randomObd2Response(0x01u, 0x04u, responseSize = 1),
        randomObd2Response(0x01u, 0x05u, responseSize = 1),
        randomObd2Response(0x01u, 0x06u, responseSize = 1),
        randomObd2Response(0x01u, 0x07u, responseSize = 1),
        randomObd2Response(0x01u, 0x08u, responseSize = 1),
        randomObd2Response(0x01u, 0x09u, responseSize = 1),
        randomObd2Response(0x01u, 0x0Au, responseSize = 1),
        randomObd2Response(0x01u, 0x0Bu, responseSize = 1),
        randomObd2Response(0x01u, 0x0Cu, responseSize = 2),
        randomObd2Response(0x01u, 0x0Du, responseSize = 1),
        randomObd2Response(0x01u, 0x0Eu, responseSize = 1),
        randomObd2Response(0x01u, 0x0Fu, responseSize = 1),
        randomObd2Response(0x01u, 0x10u, responseSize = 2),
        randomObd2Response(0x01u, 0x11u, responseSize = 1),
        obd2Response(0x01u, 0x12u) { ubyteArrayOf(0x01u) },
        randomObd2Response(0x01u, 0x13u, responseSize = 1),
        randomObd2Response(0x01u, 0x14u, responseSize = 2),
        randomObd2Response(0x01u, 0x15u, responseSize = 2),
        randomObd2Response(0x01u, 0x16u, responseSize = 2),
        randomObd2Response(0x01u, 0x17u, responseSize = 2),
        randomObd2Response(0x01u, 0x18u, responseSize = 2),
        randomObd2Response(0x01u, 0x19u, responseSize = 2),
        randomObd2Response(0x01u, 0x1Au, responseSize = 2),
        randomObd2Response(0x01u, 0x1Bu, responseSize = 2),
        obd2Response(0x01u, 0x1Cu) { ubyteArrayOf(0x06u) },
        randomObd2Response(0x01u, 0x1Du, responseSize = 1),
        randomObd2Response(0x01u, 0x1Eu, responseSize = 1),
        randomObd2Response(0x01u, 0x1Fu, responseSize = 2),

        // Service 03
        obd2Response(0x03u) {
            internalState.storedDiagnosticTroubleCodes.let {
                buildList {
                    add(it.size.toUByte())

                    for (code in it) {
                        add(code.toUInt().shr(8).toUByte())
                        add(code.toUByte())
                    }
                }.toUByteArray()
            }
        },

        // Service 04
        obd2Response(0x04u) {
            coroutineScope {
                // Simulate delay
                val delayJob = launch {
                    delay(2000L)
                }

                internalState = internalState.copy(
                    storedDiagnosticTroubleCodes = listOf(),
                )

                delayJob.join()

                ubyteArrayOf()
            }
        },

        // Service 09
        obd2Response(0x09u, 0x00u) { ubyteArrayOf(0x55u, 0x40u, 0x00u, 0x00u) },
        obd2Response(0x09u, 0x01u) { ubyteArrayOf(0x01u) },
        obd2Response(0x09u, 0x02u) {
            ubyteArrayOf(
                0x01u, 0x57u, 0x50u, 0x30u,
                0x5Au, 0x5Au, 0x5Au, 0x39u, 0x39u, 0x5Au, 0x54u,
                0x53u, 0x33u, 0x39u, 0x30u, 0x30u, 0x30u, 0x30u,
            )
        },
        obd2Response(0x09u, 0x03u) { ubyteArrayOf(0x01u) },
        obd2Response(0x09u, 0x04u) {
            ubyteArrayOf(
                0x02u, 0x33u, 0x31u, 0x32u,
                0x4Au, 0x36u, 0x30u, 0x30u, 0x30u, 0x00u, 0x00u,
                0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x41u,
                0x34u, 0x37u, 0x30u, 0x31u, 0x30u, 0x30u, 0x30u,
                0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
                0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
            )
        },
        obd2Response(0x09u, 0x06u) {
            ubyteArrayOf(
                0x02u, 0x69u, 0x53u, 0xCDu,
                0x4Bu, 0x61u, 0x1Fu, 0x6Eu, 0xF2u, 0x00u, 0x00u,
            )
        },
        obd2Response(0x09u, 0x08u) {
            ubyteArrayOf(
                0x14u, 0x00u, 0x18u, 0x00u,
                0x9Au, 0x00u, 0x11u, 0x00u, 0x18u, 0x00u, 0x00u,
                0x00u, 0x00u, 0x00u, 0x14u, 0x00u, 0x18u, 0x00u,
                0x00u, 0x00u, 0x00u, 0x00u, 0x1Cu, 0x00u, 0x18u,
                0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
                0x00u, 0x00u, 0x0Cu, 0x00u, 0x18u, 0x00u, 0x00u,
                0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
            )
        },
        obd2Response(0x09u, 0x0Au) {
            ubyteArrayOf(
                0x01u, 0x45u, 0x43u, 0x4Du,
                0x00u, 0x2Du, 0x45u, 0x6Eu, 0x67u, 0x69u, 0x6Eu,
                0x65u, 0x43u, 0x6Fu, 0x6Eu, 0x74u, 0x72u, 0x6Fu,
                0x6Cu, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u, 0x00u,
            )
        }
    )

    private val processorJob = coroutineScope.launch {
        while (true) {
            receiveChannel.receive()

            while (true) {
                when (val lineBreakIndex = receiveBuffer.indexOf('\r'.code.toByte())) {
                    -1L -> break

                    else -> {
                        // Read everything before the line break
                        val command = receiveBuffer.readString(lineBreakIndex)

                        // Then discard the line break
                        receiveBuffer.skip(1)

                        val response = processCommand(command)

                        transferBuffer.writeString(response)

                        if (!transferBuffer.exhausted()) {
                            transferChannel.trySend(Unit)
                        }
                    }
                }
            }
        }
    }

    override fun readAtMostTo(
        sink: Buffer,
        byteCount: Long,
    ) = runBlocking {
        transferChannel.receive()
    }.let {
        transferBuffer.readAtMostTo(sink, byteCount)
    }

    override fun write(
        source: Buffer,
        byteCount: Long,
    ) = receiveBuffer.write(source, byteCount).also {
        if (byteCount > 0) {
            receiveChannel.trySend(Unit)
        }
    }

    override fun flush() = receiveBuffer.flush()

    override fun close() {
        // Cancel the processor job
        processorJob.cancel()

        // Close the buffers
        receiveBuffer.close()
        transferBuffer.close()
    }

    suspend fun processCommand(message: String): String {
        val internalState = internalState

        val command = message.trim()

        val response = getCommandResponse(
            command.toUpperCasePreservingASCIIRules()
        ) ?: listOf(UNKNOWN_COMMAND)

        return buildString {
            if (internalState.echoEnabled) {
                append(command)
                append('\r')
            }

            response.forEach {
                append(it)
                append('\r')
            }

            if (lastOrNull() != '\r') {
                append('\r')
            }

            append(IDLE_MESSAGE)
        }
    }

    private suspend fun getCommandResponse(command: String) = when {
        command.startsWith(AT_PREFIX) -> {
            val atCommand = command.substringAfter(AT_PREFIX).trim()

            atResponses[atCommand]?.invoke() ?: run {
                Logger.warn(LOG_TAG) { "Unknown AT command: $atCommand" }
                null
            }
        }

        else -> canResponses[command]?.invoke()?.let { canResponse ->
            when (canResponse.size) {
                in 0..6 -> listOf(
                    buildString {
                        append(DEFAULT_CONTROL_MODULE_HEADER)
                        append(" ")
                        append(canResponse.size.toHexString(format = obdByteHexFormat))
                        append(" ")
                        append(canResponse.toHexString(format = obdByteHexFormat))
                    }
                )

                else -> buildList {
                    require(canResponse.size < 4095) {
                        "CAN response too long: ${canResponse.size}"
                    }

                    val totalBytes = canResponse.size.toUInt()

                    // First frame can carry 6 bytes, the rest 7
                    val firstFrameData = canResponse.take(6).toUByteArray()
                    val consecutiveFrames = canResponse.drop(6).chunked(7).map {
                        it.toUByteArray()
                    }

                    // First frame
                    add(
                        buildString {
                            append(DEFAULT_CONTROL_MODULE_HEADER)
                            append(" ")
                            append(
                                ubyteArrayOf(
                                    0x10u.or(totalBytes.shr(8).and(0x0Fu)).toUByte(),
                                    totalBytes.and(0xFFu).toUByte(),
                                ).toHexString(format = obdByteHexFormat)
                            )
                            append(" ")
                            append(firstFrameData.toHexString(format = obdByteHexFormat))
                        }
                    )

                    // Consecutive frames
                    consecutiveFrames.forEachIndexed { index, frame ->
                        add(
                            buildString {
                                append(DEFAULT_CONTROL_MODULE_HEADER)
                                append(" ")
                                append(
                                    0x20u
                                        .or(index.mod(15).plus(1).toUInt())
                                        .toUByte()
                                        .toHexString(format = obdByteHexFormat)
                                )
                                append(" ")
                                append(frame.toHexString(format = obdByteHexFormat))
                            }
                        )
                    }
                }
            }
        } ?: listOf("NO DATA")
    }

    companion object {
        private val LOG_TAG = Elm327Emulator::class.simpleName!!

        private const val ELM327_VERSION = "ELM327 v1.5"

        private const val AT_PREFIX = "AT"

        private const val UNKNOWN_COMMAND = "?"

        private const val IDLE_MESSAGE = ">"

        private const val DEFAULT_CONTROL_MODULE_HEADER = "7E8"

        private val obdByteHexFormat = HexFormat {
            bytes {
                byteSeparator = ""
                bytesPerGroup = 1
                groupSeparator = " "
            }

            number {
                minLength = 2
                removeLeadingZeros = true
            }

            upperCase = true
        }

        private val ignMonValues = listOf(
            "ON",
            "OFF",
        )

        private val voltageValues = listOf(
            "13.0V",
            "13.1V",
            "13.2V",
            "13.3V",
            "13.4V",
            "13.5V",
        )

        private fun obd2Response(
            vararg pid: UByte,
            response: suspend () -> UByteArray,
        ) = pid.toHexString(format = obdByteHexFormat) to suspend {
            ubyteArrayOf(
                pid.first().plus(0x40u).toUByte(), *pid.drop(1).toUByteArray(),
                *response()
            )
        }

        private fun randomObd2Response(
            vararg pid: UByte,
            responseSize: Int,
        ) = obd2Response(*pid) {
            Random.nextUBytes(responseSize)
        }
    }
}
