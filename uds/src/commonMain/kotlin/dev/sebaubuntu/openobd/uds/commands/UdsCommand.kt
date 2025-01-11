/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.uds.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.elm327.commands.CanCommand
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.uds.models.ResponseCode

/**
 * Base UDS command class.
 *
 * @param serviceCode The UDS service code
 */
@OptIn(ExperimentalUnsignedTypes::class)
sealed class UdsCommand<T>(
    private val serviceCode: UByte,
) : CanCommand<T>() {
    /**
     * Second byte of the request.
     */
    abstract val subfunction: UByte?

    /**
     * Following data of the request.
     */
    open val data: UByteArray = ubyteArrayOf()

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

    final override val commandBytes by lazy {
        buildList {
            add(serviceCode)
            subfunction?.let { add(it) }
            addAll(data)
        }.toUByteArray()
    }

    final override fun parseControlModuleResponse(rawResponse: UByteArray): Result<T, Error> {
        if (rawResponse.isEmpty()) {
            Logger.error(LOG_TAG) { "Response is empty" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        if (rawResponse[0] == NEGATIVE_RESPONSE_CODE) {
            if (rawResponse.size < 3) {
                Logger.error(LOG_TAG) { "Partial negative response" }
                return Result.Error(Error.INVALID_RESPONSE)
            }

            val serviceCode = rawResponse[1]
            if (serviceCode != this.serviceCode) {
                Logger.error(LOG_TAG) {
                    "Got a negative response for the wrong service: ${serviceCode.toHexString(format = hexFormat)}"
                }
                return Result.Error(Error.INVALID_RESPONSE)
            }

            val responseCodeByte = rawResponse[2]
            val responseCode = ResponseCode.fromUdsCode(responseCodeByte) ?: run {
                Logger.error(LOG_TAG) {
                    "Invalid response code: ${responseCodeByte.toHexString(format = hexFormat)}"
                }
                return Result.Error(Error.INVALID_RESPONSE)
            }

            Logger.error(LOG_TAG) {
                val data = rawResponse.drop(3).toUByteArray()
                "Negative response: $responseCode, data: ${data.toHexString(format = hexFormat)}"
            }

            return Result.Error(Error.INVALID_RESPONSE)
        }

        val requestCode = (rawResponse[0] - SERVICE_CODE_RESPONSE_OFFSET).toUByte()
        if (requestCode != serviceCode) {
            Logger.error(LOG_TAG) {
                "Invalid service code response: ${requestCode}, expected $serviceCode"
            }
        }

        // Check the subfunction if not null
        subfunction?.let {
            if (rawResponse.size < 2) {
                Logger.error(LOG_TAG) { "Response is too short, missing subfunction" }
                return Result.Error(Error.INVALID_RESPONSE)
            }

            if (rawResponse[1] != it) {
                Logger.error(LOG_TAG) {
                    "Invalid subfunction response: ${rawResponse[1]}, expected $it"
                }
                return Result.Error(Error.INVALID_RESPONSE)
            }
        }

        val data = rawResponse.drop(
            when (subfunction) {
                null -> 1
                else -> 2
            }
        ).toUByteArray()

        expectedDataBytes?.let {
            if (data.size != it) {
                Logger.error(LOG_TAG) { "Invalid data response size: ${data.size}, expected $it" }
                return Result.Error(Error.INVALID_RESPONSE)
            }
        }

        return parseResponse(data)
    }

    companion object {
        private val LOG_TAG = UdsCommand::class.simpleName!!

        private const val NEGATIVE_RESPONSE_CODE: UByte = 0x7Fu

        private const val SERVICE_CODE_RESPONSE_OFFSET: UByte = 0x40u

        private val hexFormat = HexFormat {
            bytes {
                bytesPerGroup = 1
                groupSeparator = " "
            }
            upperCase = true
        }
    }
}
