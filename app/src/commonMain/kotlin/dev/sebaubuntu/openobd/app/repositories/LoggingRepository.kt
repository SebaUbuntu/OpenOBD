/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import dev.sebaubuntu.openobd.logging.LogBuffer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

/**
 * Logging repository.
 */
class LoggingRepository(
    private val logBuffer: LogBuffer,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : Repository(coroutineScope, coroutineDispatcher) {
    val logEntries = logBuffer.logEntries
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList(),
        )

    fun clearAllEntries() = logBuffer.clearAllEntries()
}
