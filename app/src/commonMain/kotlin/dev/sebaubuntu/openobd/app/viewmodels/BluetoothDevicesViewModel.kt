/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import dev.sebaubuntu.openobd.app.repositories.BluetoothRepository
import dev.sebaubuntu.openobd.app.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.backend.models.BluetoothDevice

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
