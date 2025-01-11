/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
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
     * Manager state.
     */
    enum class State {
        /**
         * Manager not supported in this device.
         */
        UNAVAILABLE,

        /**
         * The manager is disabled.
         */
        DISABLED,

        /**
         * The manager is being disabled.
         */
        DISABLING,

        /**
         * The manager is being enabled.
         */
        ENABLING,

        /**
         * The manager is enabled.
         */
        ENABLED,
    }

    /**
     * Whether the manager can be enabled and/or disabled.
     */
    val isToggleable: Boolean

    /**
     * Flow of the current state of the manager.
     */
    fun state(): Flow<State>

    /**
     * Flow representing the list of devices.
     */
    fun devices(): Flow<Result<DevicesState<D, ID>, Error>>

    /**
     * Flow representing a device.
     */
    fun device(identifier: ID): Flow<Result<D, Error>>

    /**
     * Flow representing a connection to the device.
     */
    fun connection(identifier: ID): Flow<Result<Socket, Error>>

    /**
     * Set the current state of the manager.
     *
     * @param state The new state, true if enabled, false if disabled
     */
    fun setState(state: Boolean): Result<Unit, Error>
}
