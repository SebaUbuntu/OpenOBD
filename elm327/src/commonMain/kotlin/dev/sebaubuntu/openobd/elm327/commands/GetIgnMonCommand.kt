/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.elm327.Command

/**
 * Get the ignition monitoring status.
 */
object GetIgnMonCommand : Command<Boolean> {
    override val command = "AT IGN"
    override fun parseResponse(response: List<String>) = response.getOrNull(0)?.let {
        when (it) {
            "ON" -> Result.Success(true)
            "OFF" -> Result.Success(false)
            else -> null
        }
    } ?: Result.Error(Error.INVALID_RESPONSE)
}
