/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.elm327.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.elm327.Command

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
