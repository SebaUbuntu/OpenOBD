/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.models.Device
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

abstract class BaseDeviceRepository<DM : DeviceManager<D, ID>, D : Device<ID>, ID : Device.Identifier>(
    protected val deviceManager: DM,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : Repository(coroutineScope, coroutineDispatcher) {
    /**
     * @see DeviceManager.canBeToggled
     */
    val canBeToggled = deviceManager.canBeToggled

    /**
     * @see DeviceManager.state
     */
    val state = deviceManager.state()
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = DeviceManager.State.DISABLED,
        )

    /**
     * @see DeviceManager.devices
     */
    val devices = deviceManager.devices()
        .asFlowResult()
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    /**
     * @see DeviceManager.setState
     */
    fun setState(state: Boolean) = deviceManager.setState(state)

    /**
     * @see DeviceManager.device
     */
    fun device(identifier: ID) = deviceManager.device(identifier)

    /**
     * @see DeviceManager.connection
     */
    fun connection(identifier: ID) = deviceManager.connection(identifier)
}
