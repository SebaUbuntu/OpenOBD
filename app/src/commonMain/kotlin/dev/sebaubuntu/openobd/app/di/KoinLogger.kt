/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.di

import dev.sebaubuntu.openobd.logging.LogLevel
import dev.sebaubuntu.openobd.logging.Logger
import org.koin.core.logger.KOIN_TAG
import org.koin.core.logger.Level
import org.koin.core.logger.MESSAGE

/**
 * [Logger] based Koin logger.
 */
object KoinLogger : org.koin.core.logger.Logger() {
    private const val LOG_TAG = KOIN_TAG

    override fun display(level: Level, msg: MESSAGE) {
        level.toLogLevel()?.also {
            Logger.log(
                level = it,
                tag = LOG_TAG,
                throwable = null,
            ) { msg }
        }
    }

    private fun Level.toLogLevel() = when (this) {
        Level.DEBUG -> LogLevel.DEBUG
        Level.INFO -> LogLevel.INFO
        Level.WARNING -> LogLevel.WARN
        Level.ERROR -> LogLevel.ERROR
        Level.NONE -> null
    }
}
