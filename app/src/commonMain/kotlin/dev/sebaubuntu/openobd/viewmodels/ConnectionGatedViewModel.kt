/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.foldLatest
import dev.sebaubuntu.openobd.repositories.ObdRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class ConnectionGatedViewModel(
    obdRepository: ObdRepository,
) : ViewModel() {
    val hasActiveConnection = obdRepository.device
        .foldLatest(
            onSuccess = { true },
            onError = { _, _ -> false },
        )
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )
}
