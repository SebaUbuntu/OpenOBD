/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import dev.sebaubuntu.openobd.backend.models.BluetoothDevice
import dev.sebaubuntu.openobd.backend.models.BluetoothLeDevice
import dev.sebaubuntu.openobd.backend.models.DemoDevice
import dev.sebaubuntu.openobd.backend.models.Device
import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.backend.models.UsbDevice
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

class DeviceConnectionRepository(
    private val bluetoothRepository: BluetoothRepository,
    private val bluetoothLeRepository: BluetoothLeRepository,
    private val demoRepository: DemoRepository,
    private val networkRepository: NetworkRepository,
    private val usbRepository: UsbRepository,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : Repository(coroutineScope, coroutineDispatcher) {
    private val deviceIdentifier = MutableStateFlow<Device.Identifier?>(null)

    /**
     * Connected device information.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val device = deviceIdentifier
        .flatMapLatest { deviceIdentifier ->
            deviceIdentifier?.let {
                it.deviceRepository.device(
                    identifier = it
                )
            } ?: flowOf(Result.Error(Error.NOT_FOUND))
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
                            identifier = it,
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

    @Suppress("UNCHECKED_CAST")
    private val <ID : Device.Identifier> ID.deviceRepository
        get() = when (this) {
            is BluetoothDevice.Identifier -> bluetoothRepository
            is BluetoothLeDevice.Identifier -> bluetoothLeRepository
            is DemoDevice.Identifier -> demoRepository
            is NetworkDevice.Identifier -> networkRepository
            is UsbDevice.Identifier -> usbRepository
        } as BaseDeviceRepository<*, *, ID>
}
