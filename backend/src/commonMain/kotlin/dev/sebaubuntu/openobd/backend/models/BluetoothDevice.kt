/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.models

/**
 * Bluetooth device.
 *
 * @param state The [State] of the device
 */
data class BluetoothDevice(
    override val identifier: Identifier,
    override val displayName: String?,
    val state: State,
) : Device<BluetoothDevice.Identifier> {
    data class Identifier(val macAddress: String) : Device.Identifier {
        override val deviceType = DeviceType.BLUETOOTH
    }

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
