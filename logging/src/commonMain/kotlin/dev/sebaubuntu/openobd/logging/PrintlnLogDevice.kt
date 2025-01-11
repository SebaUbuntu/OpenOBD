/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
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
            tag?.let {
                print("[$it] ")
            }
            print("[${level.name}] ")
            print(message())
            throwable?.let {
                println()
                print(it.stackTraceToString())
            }
            println()
        }

        false -> Unit
    }
}
