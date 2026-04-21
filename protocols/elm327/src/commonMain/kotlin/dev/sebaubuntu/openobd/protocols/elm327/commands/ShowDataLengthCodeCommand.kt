/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.elm327.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.protocols.elm327.Command

/**
 * Show or hide DLC in the response.
 *
 * @param showDataLengthCode Whether to show the DLC or not
 */
data class ShowDataLengthCodeCommand(
    val showDataLengthCode: Boolean,
) : Command<Unit> {
    override val command = when (showDataLengthCode) {
        true -> "AT D1"
        false -> "AT D0"
    }

    override fun parseResponse(response: List<String>) = Result.Success<Unit, Error>(Unit)
}
