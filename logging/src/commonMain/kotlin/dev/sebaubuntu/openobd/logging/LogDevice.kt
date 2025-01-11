/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.logging

/**
 * A device to output to.
 */
interface LogDevice {
    fun log(
        level: LogLevel,
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    )

    fun verbose(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    ) = log(LogLevel.VERBOSE, tag, throwable, message)

    fun debug(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    ) = log(LogLevel.DEBUG, tag, throwable, message)

    fun info(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    ) = log(LogLevel.INFO, tag, throwable, message)

    fun warn(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    ) = log(LogLevel.WARN, tag, throwable, message)

    fun error(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    ) = log(LogLevel.ERROR, tag, throwable, message)

    fun assert(
        tag: String? = null,
        throwable: Throwable? = null,
        message: () -> String,
    ) = log(LogLevel.ASSERT, tag, throwable, message)
}
