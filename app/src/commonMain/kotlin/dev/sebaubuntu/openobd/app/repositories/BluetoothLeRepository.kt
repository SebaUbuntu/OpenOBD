/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import dev.sebaubuntu.openobd.backend.bluetoothle.BluetoothLeManager
import dev.sebaubuntu.openobd.backend.models.BluetoothLeDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

/**
 * Bluetooth LE repository.
 */
class BluetoothLeRepository(
    deviceManager: BluetoothLeManager,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : BaseDeviceRepository<BluetoothLeManager, BluetoothLeDevice, BluetoothLeDevice.Identifier>(
    deviceManager, coroutineScope, coroutineDispatcher
)
