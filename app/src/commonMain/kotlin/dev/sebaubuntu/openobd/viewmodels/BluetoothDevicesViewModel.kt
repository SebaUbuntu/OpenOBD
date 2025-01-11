/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import dev.sebaubuntu.openobd.backend.models.BluetoothDevice
import dev.sebaubuntu.openobd.repositories.BluetoothRepository
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository

/**
 * Bluetooth devices view model.
 */
class BluetoothDevicesViewModel(
    bluetoothRepository: BluetoothRepository,
    deviceConnectionRepository: DeviceConnectionRepository,
) : BaseDevicesViewModel<BluetoothDevice, BluetoothDevice.Identifier>(
    bluetoothRepository,
    deviceConnectionRepository
)
