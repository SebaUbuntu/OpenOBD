/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.elm327.Command

/**
 * Set echo on or off.
 */
class SetEchoCommand(echo: Boolean) : Command<Unit> {
    override val command = when (echo) {
        true -> "AT E1"
        false -> "AT E0"
    }

    override fun parseResponse(response: List<String>) = Result.Success<_, Error>(Unit)
}
