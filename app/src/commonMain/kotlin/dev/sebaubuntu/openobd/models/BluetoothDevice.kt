/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

/**
 * Bluetooth device.
 *
 * @param macAddress The MAC address of the device
 * @param state The [State] of the device
 */
data class BluetoothDevice(
    override val displayName: String?,
    val macAddress: String,
    val state: State,
) : Device {
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

    override val connectionType = ConnectionType.BLUETOOTH
}
