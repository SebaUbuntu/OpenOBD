/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.elm327.commands

import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.network.elm327.Command

/**
 * Get the device description.
 */
data object GetDeviceDescriptionCommand : Command<String> {
    override val command = "AT @1"
    override fun parseResponse(response: List<String>) = Result.Success(
        response.joinToString("\n")
    )
}
