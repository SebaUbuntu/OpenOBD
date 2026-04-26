/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.elm327.commands

import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.network.elm327.Command

/**
 * Raw command.
 *
 * @param command The command
 */
data class RawCommand(
    override val command: String
) : Command<List<String>> {
    override fun parseResponse(response: List<String>) = Result.Success(response)
}
