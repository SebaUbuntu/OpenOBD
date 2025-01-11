/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.elm327.commands.CanCommand
import dev.sebaubuntu.openobd.logging.Logger

/**
 * Base OBD command class.
 *
 * @param serviceCode The OBD service code
 */
@OptIn(ExperimentalUnsignedTypes::class)
sealed class ObdCommand<T>(
    private val serviceCode: UByte,
) : CanCommand<T>() {
    /**
     * The PID of the command.
     */
    abstract val pid: UByteArray

    /**
     * The expected number of data bytes in the response. If null no checks will be done.
     * If the response doesn't have the expected number of bytes, the command will fail and
     * [parseResponse] will be never called.
     */
    open val expectedDataBytes: Int? = null

    /**
     * Parse the response given the raw bytes response.
     */
    abstract fun parseResponse(response: UByteArray): Result<T, Error>

    override val commandBytes by lazy {
        ubyteArrayOf(serviceCode) + pid
    }

    override fun parseControlModuleResponse(rawResponse: UByteArray): Result<T, Error> {
        // Basic check: We must have at least the size of the command
        if (rawResponse.size < commandBytes.size) {
            Logger.error(LOG_TAG) {
                "Response doesn't contain enough bytes: ${rawResponse.toHexString(format = hexFormat)}"
            }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        // If we know the expected number of bytes, check it
        expectedDataBytes?.let {
            val expectedResponseSize = commandBytes.size + it
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
                val expectedValue = serviceCodeResponse.toHexString(format = hexFormat)
                val actualValue = rawResponse[0].toHexString(format = hexFormat)

                "Invalid service code response: expected $expectedValue, actual $actualValue"
            }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        // Second one is the PID itself
        for (value in pid.indices) {
            if (rawResponse[value + 1] != pid[value]) {
                Logger.error(LOG_TAG) {
                    val expectedValue = pid[value].toHexString(format = hexFormat)
                    val actualValue = rawResponse[value + 1].toHexString(format = hexFormat)

                    "Invalid PID response: expected $expectedValue, actual $actualValue"
                }
                return Result.Error(Error.INVALID_RESPONSE)
            }
        }

        // Remaining bytes are the response
        val dataResponse = rawResponse.drop(commandBytes.size).toUByteArray()
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

        private val SERVICE_CODE_RESPONSE_OFFSET: UByte = 0x40u

        private val hexFormat = HexFormat {
            bytes {
                bytesPerGroup = 1
                groupSeparator = " "
            }
            upperCase = true
        }
    }
}
