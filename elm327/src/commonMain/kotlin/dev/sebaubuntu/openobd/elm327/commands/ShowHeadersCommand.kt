/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.elm327.Command

/**
 * Show or hide OBD headers in the response.
 *
 * @param showHeaders Whether to show headers or not
 */
class ShowHeadersCommand(showHeaders: Boolean) : Command<Unit> {
    override val command = when (showHeaders) {
        true -> "AT H1"
        false -> "AT H0"
    }

    override fun parseResponse(response: List<String>) = Result.Success<Unit, Error>(Unit)
}
