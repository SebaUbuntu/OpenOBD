/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
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
    override val displayName: String?,
    val hostname: String,
    val port: Int,
) : Device<NetworkDevice.Identifier> {
    data class Identifier(val networkDeviceId: Int) : Device.Identifier {
        override val deviceType = DeviceType.NETWORK
    }
}
