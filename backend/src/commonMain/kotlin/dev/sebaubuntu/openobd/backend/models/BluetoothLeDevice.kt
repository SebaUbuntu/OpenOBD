/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.models

import com.juul.kable.Advertisement

/**
 * Bluetooth LE device.
 */
data class BluetoothLeDevice(
    override val identifier: Identifier,
    override val displayName: String?,
) : Device<BluetoothLeDevice.Identifier> {
    data class Identifier(val advertisement: Advertisement) : Device.Identifier {
        override val deviceType = DeviceType.BLUETOOTH_LE

        override fun toString() = advertisement.identifier.toString()
    }
}
