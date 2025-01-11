/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.logging

import android.util.Log

actual object PlatformLogDevice : LogDevice {
    override fun log(
        level: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: () -> String,
    ) {
        level.getLogFunction()(tag, message(), throwable)
    }

    private fun LogLevel.getLogFunction(): (String?, String?, Throwable?) -> Int = when (this) {
        LogLevel.VERBOSE -> Log::v
        LogLevel.DEBUG -> Log::d
        LogLevel.INFO -> Log::i
        LogLevel.WARN -> Log::w
        LogLevel.ERROR -> Log::e
        LogLevel.ASSERT -> Log::wtf
    }
}
