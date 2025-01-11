/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result

/**
 * OBD command.
 *
 * @param T The type of the response
 */
interface Command<T> {
    /**
     * Generate the ELM327 command to execute.
     */
    val command: String

    /**
     * Parse the response of this command. It will be stripped out of the `>` character
     * representing the end of the response.
     */
    fun parseResponse(response: List<String>): Result<T, Error>
}
