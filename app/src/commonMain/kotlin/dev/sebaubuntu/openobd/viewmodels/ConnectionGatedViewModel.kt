/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.models.ConnectionStatus
import dev.sebaubuntu.openobd.repositories.ConnectionStatusRepository
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
