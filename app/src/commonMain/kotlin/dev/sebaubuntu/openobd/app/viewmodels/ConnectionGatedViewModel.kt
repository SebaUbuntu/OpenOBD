/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.app.models.ConnectionStatus
import dev.sebaubuntu.openobd.app.repositories.ConnectionStatusRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

class ConnectionGatedViewModel(
    connectionStatusRepository: ConnectionStatusRepository,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val hasActiveConnection = connectionStatusRepository.connectionStatus
        .mapLatest { it == ConnectionStatus.READY }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )
}
