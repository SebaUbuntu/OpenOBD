/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.utils

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.models.BluetoothDevicesState
import dev.sebaubuntu.openobd.models.BluetoothState
import dev.sebaubuntu.openobd.models.DeviceConnection
import dev.sebaubuntu.openobd.models.Permission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Bluetooth manager.
 * All those methods requires [Permission.BLUETOOTH].
 */
interface BluetoothManager {
    /**
     * Get whether Bluetooth is available in this device.
     */
    fun state(): Flow<BluetoothState>

    /**
     * Get a flow of the list of devices.
     */
    fun devices(): Flow<BluetoothDevicesState>

    /**
     * Connect to a device. The connection must be alive as long as the flow is active.
     *
     * @param macAddress The MAC address of the device
     * @return A flow of the connected device. If the connection fails or the device is not found,
     *   the flow will emit null
     */
    fun connect(macAddress: String): Flow<Result<DeviceConnection, Error>>

    /**
     * Enable or disable Bluetooth.
     *
     * @param isEnabled Whether to enable or disable Bluetooth
     */
    suspend fun toggle(isEnabled: Boolean): Result<Unit, Error>

    companion object {
        /**
         * Default implementation. Assumes Bluetooth isn't supported.
         */
        val DEFAULT = object : BluetoothManager {
            override fun state() = flowOf(BluetoothState.UNAVAILABLE)

            override fun devices() = flowOf(BluetoothDevicesState.EMPTY)

            override fun connect(
                macAddress: String,
            ) = flowOf(Result.Error<DeviceConnection, _>(Error.NOT_IMPLEMENTED))

            override suspend fun toggle(
                isEnabled: Boolean,
            ) = Result.Error<Unit, _>(Error.NOT_IMPLEMENTED)
        }
    }
}
