/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.models

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import kotlinx.coroutines.flow.flowOf

/**
 * Stub [DeviceManager] implementation that does nothing.
 */
abstract class StubDeviceManager<D : Device<ID>, ID : Device.Identifier> : DeviceManager<D, ID> {
    override val isToggleable = false

    override fun state() = flowOf(DeviceManager.State.UNAVAILABLE)

    override fun devices() = flowOf(Result.Error<DevicesState<D, ID>, Error>(Error.NOT_IMPLEMENTED))

    override fun device(
        identifier: ID,
    ) = flowOf(Result.Error<D, Error>(Error.NOT_IMPLEMENTED))

    override fun connection(
        identifier: ID,
    ) = flowOf(Result.Error<Socket, Error>(Error.NOT_IMPLEMENTED))

    override fun setState(
        state: Boolean,
    ) = Result.Error<Unit, Error>(Error.NOT_IMPLEMENTED)
}
