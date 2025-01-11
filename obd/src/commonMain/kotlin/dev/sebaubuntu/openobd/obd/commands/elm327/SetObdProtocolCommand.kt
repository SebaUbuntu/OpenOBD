/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.elm327

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.obd.commands.Command
import dev.sebaubuntu.openobd.obd.models.ObdProtocol

/**
 * Set OBD protocol.
 */
class SetObdProtocolCommand(obdProtocol: ObdProtocol) : Command<String> {
    override val command = "AT SP${obdProtocol.elm327Value}"
    override fun parseResponse(response: String) = Result.Success<_, Error>(response)
}
