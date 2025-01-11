/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.logging

import dev.sebaubuntu.openobd.core.models.CircularList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest

/**
 * Logger.
 */
object Logger : LogDevice {
    private const val BUFFER_SIZE = 1024

    private val latestLogEntry = MutableStateFlow<LogEntry?>(null)

    private val bufferList = CircularList<LogEntry>(BUFFER_SIZE)

    @OptIn(ExperimentalCoroutinesApi::class)
    val logEntries = latestLogEntry
        .mapLatest { bufferList }
        .flowOn(Dispatchers.IO)

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
        saveLogEntry(
            level = level,
            tag = tag,
            throwable = throwable,
            message = message,
        )
    }

    fun clearAllEntries() = bufferList.clear()

    private fun saveLogEntry(
        level: LogLevel,
        tag: String?,
        throwable: Throwable?,
        message: () -> String,
    ) {
        val logEntry = LogEntry(
            level = level,
            tag = tag,
            message = message(),
            throwable = throwable,
        )

        bufferList.add(logEntry)
        latestLogEntry.value = logEntry
    }
}
