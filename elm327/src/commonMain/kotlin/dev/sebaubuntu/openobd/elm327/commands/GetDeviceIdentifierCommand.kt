/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.elm327.Command

/**
 * Get the device identifier.
 */
object GetDeviceIdentifierCommand : Command<String> {
    override val command = "AT @2"
    override fun parseResponse(response: List<String>) = Result.Success<_, Error>(
        response.joinToString("\n")
    )
}
