/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.utils

import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothManager
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.models.DeviceConnection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class JvmBluetoothManager : BluetoothManager {
    override fun state() = flow {
        emit(BluetoothState.DISABLED)
    }

    override fun search() = flow {
        emit(BluetoothDevicesState.EMPTY)
    }

    override fun connect(macAddress: String): Flow<Result<DeviceConnection, Error>> {
        TODO("Not yet implemented")
    }

    override suspend fun toggle(isEnabled: Boolean): Result<Unit, Error> {
        TODO("Not yet implemented")
    }
}
