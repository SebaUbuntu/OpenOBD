/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.elm327

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.obd.commands.Command

/**
 * Repeat the last command.
 */
object RepeatLastCommandCommand : Command<String> {
    override val command = "AT"
    override fun parseResponse(response: String) = Result.Success<_, Error>(response)
}
