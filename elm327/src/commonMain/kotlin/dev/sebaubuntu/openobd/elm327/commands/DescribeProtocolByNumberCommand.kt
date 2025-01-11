/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.elm327.Command
import dev.sebaubuntu.openobd.elm327.models.ObdProtocol

/**
 * Describe the current protocol by its number.
 */
object DescribeProtocolByNumberCommand : Command<Pair<Boolean, ObdProtocol>> {
    private const val AUTO_PREFIX = "A"

    override val command = "AT DPN"
    override fun parseResponse(
        response: List<String>,
    ) = response.getOrNull(0)?.let {
        val auto = it.startsWith(AUTO_PREFIX)

        it.removePrefix(AUTO_PREFIX).toUIntOrNull()?.let { value ->
            ObdProtocol.fromElm327Value(value)?.let { obdProtocol ->
                Result.Success<_, Error>(auto to obdProtocol)
            }
        }
    } ?: Result.Error(Error.INVALID_RESPONSE)
}
