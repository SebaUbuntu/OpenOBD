/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd2.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.elm327.Command
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.obd2.models.ObdResponse
import dev.sebaubuntu.openobd.obd2.utils.ObdResponseParser

/**
 * Base OBD command class.
 *
 * @param serviceCode The OBD service code
 */
@OptIn(ExperimentalStdlibApi::class, ExperimentalUnsignedTypes::class)
sealed class ObdCommand<T>(
    private val serviceCode: UByte,
) : Command<ObdResponse<T>> {
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

    override fun parseResponse(response: String): Result<ObdResponse<T>, Error> {
        // Filter out unwanted data
        val dataLines = response.split("\r").mapNotNull {
            when (val line = it.trim()) {
                // Usually indicates that the command isn't supported
                "NO DATA" -> null

                // We can skip this line
                "SEARCHING..." -> null

                else -> line
            }
        }

        // Check if we have any remaining data
        if (dataLines.isEmpty()) {
            Logger.error(LOG_TAG) { "No valid data received" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        // Parse the OBD response to raw data
        val rawObdResponse = ObdResponseParser.parse(dataLines) ?: run {
            Logger.error(LOG_TAG) { "No valid responses found" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        // Check if we have at least a valid response
        if (dataLines.isEmpty()) {
            Logger.error(LOG_TAG) { "No valid responses found" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        // Map all the responses to typed responses
        val typedResponses = ObdResponse<T>(
            value = rawObdResponse.value.mapNotNull { (controlModule, response) ->
                parseControlModuleResponse(response).getOrNull()?.let {
                    controlModule to it
                }
            }.toMap(),
            messageFormat = rawObdResponse.messageFormat,
        )

        // Check if, again, we have at least a valid response left
        if (typedResponses.value.isEmpty()) {
            Logger.error(LOG_TAG) { "No valid responses found after parsing" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        return Result.Success(typedResponses)
    }

    /**
     * Given a raw response from the control module, parse it.
     *
     * @param rawResponse The raw response from the control module, minus the actual control module
     *   ID header. Should still retain service-related headers
     * @return The parsed response, or an error if the response is malformed
     */
    private fun parseControlModuleResponse(rawResponse: UByteArray): Result<T, Error> {
        // Basic check: We must have at least the size of the command
        if (rawResponse.size < fullCommand.size) {
            Logger.error(LOG_TAG) {
                "Response doesn't contain enough bytes: ${rawResponse.toHexString(format = hexFormat)}"
            }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        // If we know the expected number of bytes, check it
        expectedDataBytes?.let {
            val expectedResponseSize = fullCommand.size + it
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
            return Result.Error(Error.INVALID_RESPONSE)
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
        val dataResponse = rawResponse.drop(fullCommand.size).toUByteArray()
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
