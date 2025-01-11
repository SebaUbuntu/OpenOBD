/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.bluetooth

import dev.sebaubuntu.openobd.backend.models.BluetoothDevice
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Socket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Bluetooth manager.
 * All those methods requires Bluetooth permissions.
 */
interface BluetoothManager : DeviceManager<BluetoothDevice, BluetoothDevice.Identifier> {
    /**
     * Bluetooth adapter state.
     */
    enum class State {
        /**
         * Bluetooth not supported in this device.
         */
        UNAVAILABLE,

        /**
         * The adapter is disabled.
         */
        DISABLED,

        /**
         * The adapter is being disabled.
         */
        DISABLING,

        /**
         * The adapter is being enabled.
         */
        ENABLING,

        /**
         * The adapter is enabled.
         */
        ENABLED,
    }

    /**
     * Get whether Bluetooth is available in this device.
     */
    fun state(): Flow<State>

    /**
     * Get a flow of the list of devices.
     */
    fun search(): Flow<BluetoothSearchState>

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
            override fun device(
                identifier: BluetoothDevice.Identifier,
            ) = flowOf(Result.Error<BluetoothDevice, _>(Error.NOT_IMPLEMENTED))

            override fun connection(
                identifier: BluetoothDevice.Identifier,
            ) = flowOf(Result.Error<Socket, _>(Error.NOT_IMPLEMENTED))

            override fun state() = flowOf(State.UNAVAILABLE)

            override fun search() = flowOf(BluetoothSearchState.EMPTY)

            override suspend fun toggle(
                isEnabled: Boolean,
            ) = Result.Error<Unit, _>(Error.NOT_IMPLEMENTED)
        }
    }
}
