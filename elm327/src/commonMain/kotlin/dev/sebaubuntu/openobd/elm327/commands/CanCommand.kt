/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.elm327.Command
import dev.sebaubuntu.openobd.elm327.models.CanResponse
import dev.sebaubuntu.openobd.elm327.models.ControlModule
import dev.sebaubuntu.openobd.elm327.utils.CanResponseParser
import dev.sebaubuntu.openobd.logging.Logger

/**
 * Base CAN command.
 *
 * The response will contain each [ControlModule] response.
 */
@OptIn(ExperimentalUnsignedTypes::class)
abstract class CanCommand<T> : Command<CanResponse<T>> {
    /**
     * Get the bytes of the request. Will be used as the command.
     */
    protected abstract val commandBytes: UByteArray

    /**
     * Given a raw response from the control module, parse it.
     *
     * @param rawResponse The raw response from the control module, minus the control module headers
     * @return The parsed response, or an error if the response is malformed
     */
    protected abstract fun parseControlModuleResponse(rawResponse: UByteArray): Result<T, Error>

    final override val command by lazy {
        commandBytes.toHexString(format = hexFormat)
    }

    final override fun parseResponse(response: List<String>): Result<CanResponse<T>, Error> {
        // Check if we have any remaining data
        if (response.isEmpty()) {
            Logger.error(LOG_TAG) {
                "No valid data received for command: $command"
            }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        // Parse the CAN response to raw data
        val canResponse = CanResponseParser.parse(response) ?: run {
            Logger.error(LOG_TAG) {
                "No valid response found for command: $command"
            }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        // Check if we have at least a valid response
        if (canResponse.value.isEmpty()) {
            Logger.error(LOG_TAG) {
                "No valid response found after parsing CAN frames for command: $command"
            }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        // Map all the responses to typed responses
        val typedResponses = CanResponse<T>(
            value = canResponse.value.mapNotNull { (controlModule, response) ->
                parseControlModuleResponse(response).getOrNull()?.let {
                    controlModule to it
                }
            }.toMap(),
            messageFormat = canResponse.messageFormat,
        )

        // Check if, again, we have at least a valid response left
        if (typedResponses.value.isEmpty()) {
            Logger.error(LOG_TAG) {
                "No valid responses found after parsing raw data for command: $command"
            }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        return Result.Success(typedResponses)
    }

    companion object {
        private val LOG_TAG = CanCommand::class.simpleName!!

        protected val hexFormat = HexFormat {
            bytes {
                bytesPerGroup = 1
                groupSeparator = " "
            }
            upperCase = true
        }
    }
}
