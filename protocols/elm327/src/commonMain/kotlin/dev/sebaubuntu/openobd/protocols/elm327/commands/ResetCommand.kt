/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.elm327.commands

import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.protocols.elm327.Command

/**
 * ELM327 IC reset.
 */
data object ResetCommand : Command<Unit> {
    override val command = "AT Z"
    override fun parseResponse(response: List<String>) = Result.Success(Unit)
}
