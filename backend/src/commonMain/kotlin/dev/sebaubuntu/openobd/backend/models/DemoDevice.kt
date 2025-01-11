/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.models

/**
 * Demo device.
 */
data object DemoDevice : Device<DemoDevice.Identifier> {
    data object Identifier : Device.Identifier {
        override val deviceType = DeviceType.DEMO
    }

    override val identifier = Identifier
    override val displayName = "Demo device"
}
