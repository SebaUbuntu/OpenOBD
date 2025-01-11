/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.elm327.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.elm327.Command

/**
 * Describe the current protocol.
 */
object DescribeProtocolCommand : Command<String> {
    override val command = "AT DP"
    override fun parseResponse(response: List<String>) = Result.Success<_, Error>(
        response.joinToString("\n")
    )
}
