/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.demo

import dev.sebaubuntu.openobd.logging.Logger

object Elm327Emulator {
    private const val ELM327_VERSION = "ELM327 v1.5"

    private const val AT_PREFIX = "AT"

    private val unknownCommandResponse = "?\r\r"

    private val atResponses = mapOf(
        "@1" to listOf("OBDII to RS232 Interpreter"),
        "I" to listOf(ELM327_VERSION),
        "Z" to listOf(ELM327_VERSION),
    )

    fun processCommand(message: String): String {
        val trimmedMessage = message.trim()

        val lines = trimmedMessage.split('\r').map {
            it.trim()
        }

        if (lines.isEmpty()) {
            Logger.warn { "No command" }
            return unknownCommandResponse
        } else if (lines.size > 1) {
            Logger.warn { "Too many lines" }
            return unknownCommandResponse
        }

        val command = lines.first()

        val response = when {
            command.startsWith(AT_PREFIX) -> {
                val atCommand = command.substringAfter(AT_PREFIX).trim()

                atResponses[atCommand]
            }

            else -> null
        } ?: listOf("?")

        return buildString {
            response.forEach {
                append(it)
                append('\r')
            }
            append('\r')
        }
    }
}
