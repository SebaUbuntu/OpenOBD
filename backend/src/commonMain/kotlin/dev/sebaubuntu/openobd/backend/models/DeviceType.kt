/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.models

/**
 * [Device] type.
 */
enum class DeviceType {
    /**
     * USB.
     */
    USB,

    /**
     * Bluetooth classic.
     */
    BLUETOOTH,

    /**
     * TCP network device.
     */
    NETWORK,

    /**
     * Serial.
     */
    SERIAL,
}
