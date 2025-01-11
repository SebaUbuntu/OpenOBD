/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.models

/**
 * Demo device.
 */
data class DemoDevice(
    override val identifier: Identifier,
    override val displayName: String?,
) : Device<DemoDevice.Identifier> {
    data class Identifier(val id: Int) : Device.Identifier {
        override val deviceType = DeviceType.DEMO
    }
}
