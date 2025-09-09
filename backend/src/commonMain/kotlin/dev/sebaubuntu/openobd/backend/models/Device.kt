/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.models

/**
 * Device.
 *
 * @param ID The device ID type
 */
sealed interface Device<ID : Device.Identifier> {
    /**
     * [Device] identifier.
     */
    sealed interface Identifier {
        /**
         * The [DeviceType] of the device.
         */
        val deviceType: DeviceType
    }

    /**
     * Unique identifier of the device.
     */
    val identifier: ID

    /**
     * Name to display to the user.
     */
    val displayName: String?
}
