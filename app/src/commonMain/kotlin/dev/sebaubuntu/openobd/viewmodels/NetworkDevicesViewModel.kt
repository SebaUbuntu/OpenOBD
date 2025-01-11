/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.repositories.NetworkRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class NetworkDevicesViewModel(
    networkRepository: NetworkRepository,
    deviceConnectionRepository: DeviceConnectionRepository,
) : BaseDevicesViewModel<NetworkDevice.Identifier>(deviceConnectionRepository) {
    val devices = networkRepository.devices
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = mapOf(),
        )
}
