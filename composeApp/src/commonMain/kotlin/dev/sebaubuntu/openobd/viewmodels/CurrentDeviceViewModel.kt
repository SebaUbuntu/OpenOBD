/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.models.FlowResult
import dev.sebaubuntu.openobd.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.repositories.DevicesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class CurrentDeviceViewModel(
    devicesRepository: DevicesRepository,
) : ViewModel() {
    val obdDevice = devicesRepository.obdDevice
        .asFlowResult()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            FlowResult.Loading()
        )
}
