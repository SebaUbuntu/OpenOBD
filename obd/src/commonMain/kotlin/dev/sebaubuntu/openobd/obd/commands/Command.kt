/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands

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
    fun parseResponse(response: String): Result<T, Error>
}
