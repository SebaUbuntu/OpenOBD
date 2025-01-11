/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.obd

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.obd.commands.Command
import dev.sebaubuntu.openobd.obd.models.ControlModule
import kotlin.jvm.JvmInline

/**
 * Base OBD command class.
 */
@OptIn(ExperimentalStdlibApi::class, ExperimentalUnsignedTypes::class)
sealed class ObdCommand<T>(
    private val serviceCode: UByte,
) : Command<T> {
    @JvmInline
    value class ControlModuleResponses<T>(
        private val value: Map<ControlModule, T>
    )

    /**
     * Get the PID of the command.
     */
    abstract val pid: UByteArray

    /**
     * Get the expected number of data bytes in the response. If null no checks will be done.
     * If the response doesn't have the expected number of bytes, the command will fail and
     * [parseResponse] will be never called.
     */
    open val expectedDataBytes: Int? = null

    /**
     * Parse the response given the raw bytes response.
     */
    abstract fun parseResponse(response: UByteArray): Result<T, Error>

    private val fullCommand by lazy {
        ubyteArrayOf(serviceCode) + pid
    }

    override val command by lazy {
        fullCommand.toHexString(format = hexFormat)
    }

    override fun parseResponse(response: String): Result<T, Error> {
        if (!response.matches(HEX_RESPONSE_REGEX)) {
            when (response) {
                "NO DATA" -> Logger.info(LOG_TAG, Throwable()) { "Got empty response" }
                else -> Logger.error(LOG_TAG) { "Invalid response: $response" }
            }

            return Result.Error(Error.INVALID_RESPONSE)
        }

        val rawResponse = runCatching {
            response.hexToUByteArray(format = hexFormat)
        }.getOrElse {
            Logger.error(LOG_TAG) { "Cannot parse response to byte array: $response" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        // Size checks
        val commandBytesSize = fullCommand.size

        // Basic check: We must have at least the size of the command
        if (rawResponse.size < commandBytesSize) {
            Logger.error(LOG_TAG) {
                "Response doesn't contain enough bytes: ${rawResponse.toHexString(format = hexFormat)}"
            }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        // If we know the expected number of bytes, check it
        expectedDataBytes?.let {
            val expectedResponseSize = commandBytesSize + it
            if (rawResponse.size != expectedResponseSize) {
                Logger.error(LOG_TAG) {
                    "Unexpected response size: Expected $expectedResponseSize bytes, got ${rawResponse.size}"
                }
                return Result.Error(Error.INVALID_RESPONSE)
            }
        }

        // First one indicates the initial PID it's responding to
        val serviceCodeResponse = rawResponse[0].or(SERVICE_CODE_RESPONSE_OFFSET)
        if (rawResponse[0] != serviceCodeResponse) {
            Logger.error(LOG_TAG) {
                "Invalid service code response: ${rawResponse[0]}, expected $serviceCodeResponse"
            }
        }

        // Second one is the PID itself
        for (value in pid.indices) {
            if (rawResponse[value + 1] != pid[value]) {
                Logger.error(LOG_TAG) {
                    "Invalid PID response: ${rawResponse[value + 1]}, expected ${pid[value]}"
                }
                return Result.Error(Error.INVALID_RESPONSE)
            }
        }

        // Remaining bytes are the response
        val dataResponse = rawResponse.drop(commandBytesSize).toUByteArray()
        expectedDataBytes?.let {
            if (dataResponse.size != it) {
                Logger.error(LOG_TAG) {
                    "Invalid data response size: ${dataResponse.size}, expected $it"
                }
                return Result.Error(Error.INVALID_RESPONSE)
            }
        }

        return parseResponse(dataResponse)
    }

    companion object {
        private val LOG_TAG = ObdCommand::class.simpleName!!

        private val HEX_RESPONSE_REGEX = "[0-9a-fA-F]{2}(\\s+[0-9a-zA-Z]{2})+".toRegex()

        private val SERVICE_CODE_RESPONSE_OFFSET = 0x40U.toUByte()

        private val hexFormat = HexFormat {
            bytes {
                bytesPerGroup = 1
                groupSeparator = " "
            }
            upperCase = true
        }
    }
}
