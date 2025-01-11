/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.logging

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime

/**
 * Logger.
 */
@OptIn(ExperimentalTime::class)
object Logger : LogDevice {
    private val _logEntryFlow = MutableSharedFlow<LogEntry>()
    val logEntryFlow = _logEntryFlow.asSharedFlow()

    override fun log(
        level: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: () -> String,
    ) = PlatformLogDevice.log(
        level = level,
        tag = tag,
        throwable = throwable,
        message = message,
    ).also {
        runBlocking {
            _logEntryFlow.emit(
                LogEntry(
                    message = message(),
                    level = level,
                    tag = tag,
                    throwable = throwable,
                )
            )
        }
    }
}
