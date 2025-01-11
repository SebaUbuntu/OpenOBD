/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.elm327

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.obd.commands.Command

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

    override fun parseResponse(response: String) = Result.Success<Unit, Error>(Unit)
}
