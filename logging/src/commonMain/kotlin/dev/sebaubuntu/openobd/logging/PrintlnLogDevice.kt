/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.logging

/**
 * A log device that prints to stdout.
 */
object PrintlnLogDevice : LogDevice {
    private val level = LogLevel.INFO

    override fun log(
        level: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: () -> String,
    ) = when (level >= PrintlnLogDevice.level) {
        true -> {
            buildString {
                tag?.also {
                    append("[$it] ")
                }
                append("[${level.name}] ")
                append(message())
                throwable?.also {
                    appendLine()
                    append(it.stackTraceToString())
                }
            }.let {
                println(it)
            }
        }

        false -> Unit
    }
}
