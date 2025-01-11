/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

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
