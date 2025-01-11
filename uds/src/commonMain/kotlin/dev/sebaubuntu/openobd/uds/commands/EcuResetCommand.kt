/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.uds.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.uds.models.ResetType

/**
 * UDS service 11: ECU reset command.
 *
 * @param resetType The type of reset to perform
 */
@OptIn(ExperimentalUnsignedTypes::class)
class EcuResetCommand(
    private val resetType: ResetType,
) : UdsCommand<EcuResetCommand.Response>(0x11u) {
    /**
     * [EcuResetCommand] response.
     *
     * @param resetTypeEcho The type of reset that was echoed
     * @param powerdownTime The power down time if the reset type is
     *   [ResetType.ENABLE_RAPID_POWER_SHUT_DOWN]
     */
    data class Response(
        val resetTypeEcho: ResetType,
        val powerdownTime: UByte?,
    )

    override val subfunction = resetType.subfunction

    override fun parseResponse(response: UByteArray): Result<Response, Error> {
        if (response.isEmpty()) {
            Logger.info(LOG_TAG) { "Response is empty" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        val resetTypeEcho = ResetType.entries.firstOrNull {
            it.subfunction == response[0]
        } ?: run {
            Logger.info(LOG_TAG) { "Invalid reset type echo: ${response[0]}" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        val powerdownTime = when (resetType) {
            ResetType.ENABLE_RAPID_POWER_SHUT_DOWN -> response.getOrNull(1) ?: run {
                Logger.info(LOG_TAG) { "No power down time found" }
                return Result.Error(Error.INVALID_RESPONSE)
            }

            else -> null
        }

        return Result.Success(
            Response(
                resetTypeEcho = resetTypeEcho,
                powerdownTime = powerdownTime,
            )
        )
    }

    companion object {
        private val LOG_TAG = EcuResetCommand::class.simpleName!!
    }
}
