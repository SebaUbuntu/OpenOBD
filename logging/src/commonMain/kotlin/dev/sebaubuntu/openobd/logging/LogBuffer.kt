/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.logging

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * A class that collects log entries.
 */
class LogBuffer(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) {
    private val _logEntries = MutableSharedFlow<LogEntry>(
        replay = BUFFER_SIZE,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val logEntries = _logEntries
        .mapLatest { _logEntries.replayCache }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList(),
        )

    init {
        coroutineScope.launch(coroutineDispatcher) {
            Logger.logEntryFlow.collect {
                _logEntries.emit(it)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun clearAllEntries() = _logEntries.resetReplayCache()

    companion object {
        private const val BUFFER_SIZE = 1024
    }
}
