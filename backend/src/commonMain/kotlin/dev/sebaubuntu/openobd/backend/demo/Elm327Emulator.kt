/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.demo

import dev.sebaubuntu.openobd.logging.Logger

object Elm327Emulator {
    private val LOG_TAG = Elm327Emulator::class.simpleName!!

    private const val ELM327_VERSION = "ELM327 v1.5"

    private const val AT_PREFIX = "AT"

    private const val UNKNOWN_COMMAND = "?"

    private const val IDLE_MESSAGE = ">"

    private val atResponses = mapOf(
        "@1" to listOf("OBDII to RS232 Interpreter"),
        "DP" to listOf("AUTO, ISO 15765-4 (CAN 11/500)"),
        "DPN" to listOf("A6"),
        "E0" to listOf("OK"),
        "H1" to listOf("OK"),
        "I" to listOf(ELM327_VERSION),
        "IGN" to listOf("ON"),
        "RV" to listOf("13.5V"),
        "SP0" to listOf("OK"),
        "Z" to listOf(ELM327_VERSION),
    )

    fun processCommand(message: String): String {
        val trimmedMessage = message.trim()

        val lines = trimmedMessage.split('\r').map {
            it.trim()
        }

        val command = when (lines.size) {
            0 -> {
                Logger.warn(LOG_TAG) { "No lines" }
                null
            }

            1 -> lines.first().trim()

            else -> {
                Logger.warn(LOG_TAG) { "Too many lines" }
                null
            }
        }

        val response = command?.let {
            getCommandResponse(it)
        } ?: listOf(UNKNOWN_COMMAND)

        return buildString {
            response.forEach {
                append(it)
                append('\r')
            }
            append('\r')
            append(IDLE_MESSAGE)
        }
    }

    private fun getCommandResponse(command: String) = when {
        command.startsWith(AT_PREFIX) -> {
            val atCommand = command.substringAfter(AT_PREFIX).trim()

            atResponses[atCommand] ?: run {
                Logger.warn(LOG_TAG) { "Unknown AT command: $atCommand" }
                null
            }
        }

        else -> null
    }
}
