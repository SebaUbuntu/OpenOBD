/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.elm327.Command

/**
 * Get the device description.
 */
object GetDeviceDescriptionCommand : Command<String> {
    override val command = "AT @1"
    override fun parseResponse(response: List<String>) = Result.Success<_, Error>(
        response.joinToString("\n")
    )
}
