/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.models

/**
 * [Device] type.
 */
enum class DeviceType {
    /**
     * Bluetooth classic.
     */
    BLUETOOTH,

    /**
     * Bluetooth LE.
     */
    BLUETOOTH_LE,

    /**
     * TCP network device.
     */
    NETWORK,

    /**
     * Serial.
     */
    SERIAL,

    /**
     * USB.
     */
    USB,

    /**
     * Demo.
     */
    DEMO,
}
