/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

/**
 * A OBD device that can be connected to.
 *
 * @param name The name of the device
 * @param connectionType The connection type of the device
 * @param socket The stream of the device, managed by the backend
 */
data class ObdDevice(
    val name: String?,
    val connectionType: ConnectionType,
    val socket: Socket,
) {
    interface Socket {
        fun read(byteArray: ByteArray): Int
        fun write(data: ByteArray)
    }
}
