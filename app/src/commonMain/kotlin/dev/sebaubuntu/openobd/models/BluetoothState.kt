/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

/**
 * Bluetooth adapter state.
 */
enum class BluetoothState {
    /**
     * Bluetooth not supported in this device.
     */
    UNAVAILABLE,

    /**
     * The adapter is disabled.
     */
    DISABLED,

    /**
     * The adapter is being disabled.
     */
    DISABLING,

    /**
     * The adapter is being enabled.
     */
    ENABLING,

    /**
     * The adapter is enabled.
     */
    ENABLED,
}
