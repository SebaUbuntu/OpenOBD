/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.network

import dev.sebaubuntu.openobd.backend.models.DeviceType
import dev.sebaubuntu.openobd.backend.models.Device
import dev.sebaubuntu.openobd.backend.models.DeviceIdentifier

/**
 * Network device.
 *
 * @param hostname The host of the device
 * @param port The port of the device
 */
data class NetworkDevice(
    override val deviceIdentifier: Identifier,
    override val displayName: String?,
    val hostname: String,
    val port: Int,
) : Device<NetworkDevice.Identifier> {
    data class Identifier(val networkDeviceId: Int) : DeviceIdentifier(DeviceType.NETWORK)
}
