/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.elm327.commands

import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.network.elm327.Command
import dev.sebaubuntu.openobd.network.elm327.models.ObdProtocol

/**
 * Set OBD protocol.
 */
data class SetObdProtocolCommand(val obdProtocol: ObdProtocol) : Command<Unit> {
    override val command = "AT SP${obdProtocol.elm327Value}"
    override fun parseResponse(response: List<String>) = Result.Success(Unit)
}
