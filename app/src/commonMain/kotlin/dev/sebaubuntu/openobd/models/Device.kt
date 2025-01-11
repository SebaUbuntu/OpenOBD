/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

/**
 * Generic device interface.
 */
sealed interface Device {
    /**
     * Name to display to the user.
     */
    val displayName: String?

    /**
     * Connection type.
     */
    val connectionType: ConnectionType
}
