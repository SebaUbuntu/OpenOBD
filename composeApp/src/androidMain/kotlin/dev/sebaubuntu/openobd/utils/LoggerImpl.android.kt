/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.utils

import android.util.Log

actual object LoggerImpl {
    actual inline fun log(
        level: Logger.Level,
        tag: String?,
        throwable: Throwable?,
        message: () -> String,
    ) {
        level.getLogFunction()(tag, message(), throwable)
    }

    fun Logger.Level.getLogFunction(): (String?, String?, Throwable?) -> Int = when (this) {
        Logger.Level.VERBOSE -> Log::v
        Logger.Level.DEBUG -> Log::d
        Logger.Level.INFO -> Log::i
        Logger.Level.WARN -> Log::w
        Logger.Level.ERROR -> Log::e
        Logger.Level.ASSERT -> Log::wtf
    }
}
