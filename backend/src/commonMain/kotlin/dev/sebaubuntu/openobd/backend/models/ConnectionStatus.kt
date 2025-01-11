/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.models

import dev.sebaubuntu.openobd.core.models.Socket

/**
 * Connection status.
 *
 * @param D The [Device] type
 */
sealed interface ConnectionStatus<D : Device<ID>, ID : Device.Identifier> {
    /**
     * The device associated with this connection.
     */
    val device: D?

    /**
     * The [Socket] associated with this connection.
     */
    val socket: Socket?
        get() = null

    /**
     * No device connected.
     */
    class Idle<D : Device<ID>, ID : Device.Identifier> : ConnectionStatus<D, ID> {
        override val device: D? = null
    }

    /**
     * The device is connecting.
     */
    data class Connecting<D : Device<ID>, ID : Device.Identifier>(
        override val device: D?,
    ) : ConnectionStatus<D, ID>

    /**
     * The connection is alive.
     *
     * @param socket The [Socket]
     */
    data class Connected<D : Device<ID>, ID : Device.Identifier>(
        override val device: D,
        override val socket: Socket,
    ) : ConnectionStatus<D, ID>

    /**
     * Connection lost with the device.
     */
    data class Lost<D : Device<ID>, ID : Device.Identifier>(
        override val device: D?,
    ) : ConnectionStatus<D, ID>
}
