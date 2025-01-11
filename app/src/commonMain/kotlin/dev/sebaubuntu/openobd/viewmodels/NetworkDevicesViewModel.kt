/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.models.ConnectionType
import dev.sebaubuntu.openobd.repositories.NetworkRepository
import dev.sebaubuntu.openobd.repositories.ObdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NetworkDevicesViewModel(
    networkRepository: NetworkRepository,
    private val obdRepository: ObdRepository,
) : ViewModel() {
    val devices = networkRepository.devices
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = mapOf(),
        )

    fun selectDevice(device: String) = viewModelScope.launch(Dispatchers.IO) {
        obdRepository.setDeviceIdentifier(ConnectionType.NETWORK, device)
    }
}
