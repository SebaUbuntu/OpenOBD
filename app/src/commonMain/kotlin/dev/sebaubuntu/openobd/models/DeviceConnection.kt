/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

import dev.sebaubuntu.openobd.core.models.Socket

/**
 * Current device connection.
 *
 * @param device The [Device]
 * @param socket The [Socket]
 */
data class DeviceConnection(
    val device: Device,
    val socket: Socket,
)
