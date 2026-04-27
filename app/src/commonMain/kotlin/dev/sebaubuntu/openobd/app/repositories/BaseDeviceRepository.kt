/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.network.devices.models.Device
import dev.sebaubuntu.openobd.network.devices.models.DeviceManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

abstract class BaseDeviceRepository<DM : DeviceManager<D, ID>, D : Device<ID>, ID : Device.Identifier>(
    protected val deviceManager: DM?,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : Repository(coroutineScope, coroutineDispatcher) {
    /**
     * @see DeviceManager.isToggleable
     */
    val isToggleable = deviceManager?.isToggleable ?: false

    /**
     * @see DeviceManager.state
     */
    val state = deviceManager?.state().orFlowOfValue(DeviceManager.State.UNAVAILABLE)
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = DeviceManager.State.DISABLED,
        )

    /**
     * @see DeviceManager.devices
     */
    val devices = deviceManager?.devices().orError()
        .asFlowResult()
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading,
        )

    /**
     * @see DeviceManager.device
     */
    fun device(identifier: ID) = deviceManager?.device(identifier).orError()

    /**
     * @see DeviceManager.connection
     */
    fun connection(identifier: ID) = deviceManager?.connection(identifier).orError()

    /**
     * @see DeviceManager.setState
     */
    fun setState(
        state: Boolean,
    ) = deviceManager?.setState(state) ?: Result.Failure(Error.NOT_IMPLEMENTED)

    private fun <T> Flow<T>?.orFlowOfValue(value: T) = this ?: flowOf(value)

    private fun <T> Flow<Result<T, Error>>?.orError() = orFlowOfValue(
        Result.Failure(Error.NOT_IMPLEMENTED)
    )
}
