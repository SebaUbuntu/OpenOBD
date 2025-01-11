/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.models

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import kotlinx.coroutines.flow.Flow

/**
 * Device manager.
 *
 * @param D The [Device] type
 * @param ID The [Device.Identifier] type
 */
interface DeviceManager<D : Device<ID>, ID : Device.Identifier> {
    /**
     * Create a flow representing a device.
     */
    fun device(identifier: ID): Flow<Result<D, Error>>

    /**
     * Create a flow representing a connection to the device.
     */
    fun connection(identifier: ID): Flow<Result<Socket, Error>>
}
