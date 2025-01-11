/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.backend.models.DemoDevice
import dev.sebaubuntu.openobd.repositories.DemoRepository
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class DemoDevicesViewModel(
    demoRepository: DemoRepository,
    deviceConnectionRepository: DeviceConnectionRepository,
) : BaseDevicesViewModel<DemoDevice.Identifier>(deviceConnectionRepository) {
    val devices = demoRepository.devices
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf(),
        )
}
