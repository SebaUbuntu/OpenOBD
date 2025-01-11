/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.models

/**
 * Network device.
 *
 * @param hostname The host of the device
 * @param port The port of the device
 */
data class NetworkDevice(
    override val identifier: Identifier,
    override val displayName: String,
    val hostname: String,
    val port: Int,
) : Device<NetworkDevice.Identifier> {
    data class Identifier(val networkDeviceId: Long) : Device.Identifier {
        override val deviceType = DeviceType.NETWORK
    }

    companion object {
        fun dev.sebaubuntu.openobd.storage.database.entities.NetworkDevice.toModel() =
            NetworkDevice(
                identifier = Identifier(networkDeviceId),
                displayName = displayName,
                hostname = hostname,
                port = port,
            )
    }
}
