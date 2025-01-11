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
interface Device<ID : DeviceIdentifier> {
    /**
     * Unique identifier of the device.
     */
    val deviceIdentifier: ID

    /**
     * Name to display to the user.
     */
    val displayName: String?
}
