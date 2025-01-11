/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.bluetooth

import dev.sebaubuntu.openobd.backend.models.BluetoothDevice
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.backend.models.StubDeviceManager

/**
 * Bluetooth manager.
 * All those methods requires Bluetooth permissions.
 */
interface BluetoothManager : DeviceManager<BluetoothDevice, BluetoothDevice.Identifier> {
    companion object {
        /**
         * Default implementation. Assumes Bluetooth isn't supported.
         */
        val DEFAULT: BluetoothManager = object : BluetoothManager,
            StubDeviceManager<BluetoothDevice, BluetoothDevice.Identifier>() {}
    }
}
