/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class CurrentDeviceViewModel(
    deviceConnectionRepository: DeviceConnectionRepository,
) : ViewModel() {
    val device = deviceConnectionRepository.device
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            null
        )
}
