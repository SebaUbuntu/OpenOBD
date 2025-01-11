/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.models.BluetoothDevice
import dev.sebaubuntu.openobd.models.BluetoothDevicesState
import dev.sebaubuntu.openobd.models.BluetoothState
import dev.sebaubuntu.openobd.models.ConnectionType
import dev.sebaubuntu.openobd.repositories.BluetoothRepository
import dev.sebaubuntu.openobd.repositories.ObdRepository
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
    private val obdRepository: ObdRepository,
) : ViewModel() {
    val state = bluetoothRepository.state
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = BluetoothState.UNAVAILABLE,
        )

    val discover = bluetoothRepository.devices
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = BluetoothDevicesState.EMPTY,
        )

    fun toggleBluetooth(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        bluetoothRepository.toggleBluetooth(enabled)
    }

    fun selectDevice(bluetoothDevice: BluetoothDevice) = viewModelScope.launch(Dispatchers.IO) {
        obdRepository.setDeviceIdentifier(ConnectionType.BLUETOOTH, bluetoothDevice.macAddress)
    }
}
