/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.elm327

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.obd.commands.Command

/**
 * Set echo on or off.
 */
class SetEchoCommand(echo: Boolean) : Command<String> {
    override val command = when (echo) {
        true -> "AT E1"
        false -> "AT E0"
    }

    override fun parseResponse(response: String) = Result.Success<_, Error>(response)
}
