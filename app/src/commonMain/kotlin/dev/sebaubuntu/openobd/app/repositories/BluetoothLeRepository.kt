/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import dev.sebaubuntu.openobd.network.devices.bluetoothle.BluetoothLeManager
import dev.sebaubuntu.openobd.network.devices.models.BluetoothLeDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Single

/**
 * Bluetooth LE repository.
 */
@Single
class BluetoothLeRepository(
    deviceManager: BluetoothLeManager?,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : BaseDeviceRepository<BluetoothLeManager, BluetoothLeDevice, BluetoothLeDevice.Identifier>(
    deviceManager, coroutineScope, coroutineDispatcher
)
