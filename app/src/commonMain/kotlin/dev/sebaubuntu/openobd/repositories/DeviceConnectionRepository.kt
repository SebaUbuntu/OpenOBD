/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.models.BluetoothDevice
import dev.sebaubuntu.openobd.backend.models.DemoDevice
import dev.sebaubuntu.openobd.backend.models.Device
import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

class DeviceConnectionRepository(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val bluetoothRepository: BluetoothRepository,
    private val networkRepository: NetworkRepository,
) {
    private val deviceIdentifier = MutableStateFlow<Device.Identifier?>(null)

    /**
     * Connected device information.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val device = deviceIdentifier
        .flatMapLatest { deviceIdentifier ->
            deviceIdentifier?.deviceRepository?.device(
                identifier = deviceIdentifier,
            ) ?: flowOf(Result.Error(Error.NOT_FOUND))
        }
        .asFlowResult()
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val connection = deviceIdentifier
        .flatMapLatest { deviceIdentifier ->
            deviceIdentifier?.let {
                flow {
                    emit(flowOf(FlowResult.Loading()))

                    emit(
                        it.deviceRepository.connection(
                            identifier = deviceIdentifier,
                        ).asFlowResult()
                    )
                }.flatMapLatest { it }
            } ?: flowOf(FlowResult.Error(Error.NOT_FOUND))
        }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    /**
     * Set the target device's identifier.
     */
    fun setDeviceIdentifier(deviceIdentifier: Device.Identifier?) {
        this.deviceIdentifier.value = deviceIdentifier
    }

    private val <ID : Device.Identifier> ID.deviceRepository
        get() = when (this) {
            is BluetoothDevice.Identifier -> bluetoothRepository as BaseDeviceRepository<*, ID>
            is DemoDevice.Identifier -> TODO()
            is NetworkDevice.Identifier -> networkRepository as BaseDeviceRepository<*, ID>
            else -> error("Unknown device type")
        }
}
