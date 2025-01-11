/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.utils

object Logger {
    enum class Level {
        VERBOSE,
        DEBUG,
        INFO,
        WARN,
        ERROR,
        ASSERT,
    }

    inline fun verbose(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    ) = LoggerImpl.log(Level.VERBOSE, tag, throwable, message)

    inline fun debug(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    ) = LoggerImpl.log(Level.DEBUG, tag, throwable, message)

    inline fun info(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    ) = LoggerImpl.log(Level.INFO, tag, throwable, message)

    inline fun warn(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    ) = LoggerImpl.log(Level.WARN, tag, throwable, message)

    inline fun error(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    ) = LoggerImpl.log(Level.ERROR, tag, throwable, message)

    inline fun assert(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    ) = LoggerImpl.log(Level.ASSERT, tag, throwable, message)
}
