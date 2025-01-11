/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.models

import kotlinx.coroutines.flow.Flow

/**
 * Device manager.
 *
 * @param D The [Device] type
 */
interface DeviceManager<D : Device<ID>, ID : DeviceIdentifier> {
    /**
     * Create a flow representing a connection to the device.
     */
    fun connection(deviceIdentifier: ID): Flow<ConnectionStatus<D, ID>>
}
