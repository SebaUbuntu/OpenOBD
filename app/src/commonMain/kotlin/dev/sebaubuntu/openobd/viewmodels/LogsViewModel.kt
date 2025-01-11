/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.repositories.LoggingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class LogsViewModel(
    private val loggingRepository: LoggingRepository,
) : ViewModel() {
    val logEntries = loggingRepository.logEntries
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf(),
        )

    fun clearAllEntries() = loggingRepository.clearAllEntries()
}
