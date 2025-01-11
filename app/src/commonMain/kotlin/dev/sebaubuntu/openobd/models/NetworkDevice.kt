/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

/**
 * Network device.
 *
 * @param hostname The host of the device
 * @param port The port of the device
 */
data class NetworkDevice(
    override val displayName: String?,
    val hostname: String,
    val port: Int,
) : Device {
    override val connectionType = ConnectionType.NETWORK
}
