/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothManager
import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothSearchState
import dev.sebaubuntu.openobd.backend.models.BluetoothDevice
import dev.sebaubuntu.openobd.repositories.BluetoothRepository
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Bluetooth devices view model.
 */
class BluetoothDevicesViewModel(
    private val bluetoothRepository: BluetoothRepository,
    deviceConnectionRepository: DeviceConnectionRepository,
) : BaseDevicesViewModel<BluetoothDevice.Identifier>(deviceConnectionRepository) {
    val state = bluetoothRepository.state
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = BluetoothManager.State.UNAVAILABLE,
        )

    val discover = bluetoothRepository.devices
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = BluetoothSearchState.EMPTY,
        )

    fun toggleBluetooth(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        bluetoothRepository.toggleBluetooth(enabled)
    }
}
