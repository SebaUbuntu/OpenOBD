/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.models

/**
 * USB device.
 *
 * @param vendorId The vendor ID of the device
 * @param productId The product ID of the device
 */
data class UsbDevice(
    override val identifier: Identifier,
    override val displayName: String?,
    val vendorId: UInt,
    val productId: UInt,
) : Device<UsbDevice.Identifier> {
    data class Identifier(
        val id: Int, // TODO
    ) : Device.Identifier {
        override val deviceType = DeviceType.USB
    }
}
