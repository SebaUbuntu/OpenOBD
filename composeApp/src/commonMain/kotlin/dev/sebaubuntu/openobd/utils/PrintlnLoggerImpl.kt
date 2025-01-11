/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.utils

/**
 * A logger that prints to stdout.
 */
object PrintlnLoggerImpl {
    val level = Logger.Level.INFO

    inline fun log(
        level: Logger.Level,
        tag: String?,
        throwable: Throwable?,
        message: () -> String,
    ) = when (level >= this.level) {
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
