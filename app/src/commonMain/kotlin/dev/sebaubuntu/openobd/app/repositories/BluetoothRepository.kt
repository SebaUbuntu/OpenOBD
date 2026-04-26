/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import dev.sebaubuntu.openobd.network.devices.bluetooth.BluetoothManager
import dev.sebaubuntu.openobd.network.devices.models.BluetoothDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

/**
 * Bluetooth repository.
 */
class BluetoothRepository(
    deviceManager: BluetoothManager,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : BaseDeviceRepository<BluetoothManager, BluetoothDevice, BluetoothDevice.Identifier>(
    deviceManager, coroutineScope, coroutineDispatcher
)
