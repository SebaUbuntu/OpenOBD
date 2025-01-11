/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.bluetooth

import dev.sebaubuntu.openobd.backend.models.Device
import dev.sebaubuntu.openobd.backend.models.DeviceIdentifier
import dev.sebaubuntu.openobd.backend.models.DeviceType

/**
 * Bluetooth device.
 *
 * @param state The [State] of the device
 */
data class BluetoothDevice(
    override val deviceIdentifier: Identifier,
    override val displayName: String?,
    val state: State,
) : Device<BluetoothDevice.Identifier> {
    data class Identifier(val macAddress: String) : DeviceIdentifier(DeviceType.BLUETOOTH)

    enum class State {
        /**
         * Unknown state.
         */
        UNKNOWN,

        /**
         * The device has been found through discovery.
         */
        AVAILABLE,

        /**
         * The device is currently bonding.
         */
        BONDING,

        /**
         * The device has been paired before.
         */
        BONDED,

        /**
         * The device is currently connecting.
         */
        CONNECTING,

        /**
         * The device is currently connected.
         */
        CONNECTED,

        /**
         * The device is currently disconnecting.
         */
        DISCONNECTING,
    }
}
