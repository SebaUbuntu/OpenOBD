/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.models

enum class ConnectionStatus {
    /**
     * Not attempting a connection.
     */
    IDLE,

    /**
     * Connecting.
     */
    CONNECTING,

    /**
     * Connected, initializing the ELM327.
     */
    INITIALIZING,

    /**
     * Device connected and ready.
     */
    READY,

    /**
     * Connection not established.
     */
    FAILED_CONNECTION,

    /**
     * Connected, but cannot communicate with the ELM327.
     */
    FAILED_INITIALIZATION,
}
