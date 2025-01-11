/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import dev.sebaubuntu.openobd.app.repositories.BluetoothLeRepository
import dev.sebaubuntu.openobd.app.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.backend.models.BluetoothLeDevice

/**
 * Bluetooth LE devices view model.
 */
class BluetoothLeDevicesViewModel(
    bluetoothLeRepository: BluetoothLeRepository,
    deviceConnectionRepository: DeviceConnectionRepository,
) : BaseDevicesViewModel<BluetoothLeDevice, BluetoothLeDevice.Identifier>(
    bluetoothLeRepository,
    deviceConnectionRepository
)
