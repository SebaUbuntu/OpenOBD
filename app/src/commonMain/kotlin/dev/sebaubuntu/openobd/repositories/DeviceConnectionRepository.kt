/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.models.BluetoothDevice
import dev.sebaubuntu.openobd.backend.models.ConnectionStatus
import dev.sebaubuntu.openobd.backend.models.Device
import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

class DeviceConnectionRepository(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val bluetoothRepository: BluetoothRepository,
    private val networkRepository: NetworkRepository,
) {
    private val deviceIdentifier = MutableStateFlow<Device.Identifier?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val deviceConnection = deviceIdentifier
        .flatMapLatest { deviceIdentifier ->
            when (deviceIdentifier) {
                is BluetoothDevice.Identifier -> bluetoothRepository.connection(deviceIdentifier)
                is NetworkDevice.Identifier -> networkRepository.connection(deviceIdentifier)
                null -> flowOf(ConnectionStatus.Idle())
            }
        }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ConnectionStatus.Idle(),
        )

    /**
     * Connected device information.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val device = deviceConnection
        .mapLatest { it.device }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

    /**
     * Current socket.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val socket = deviceConnection
        .mapLatest { it.socket }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null,
        )

    /**
     * Set the target device's identifier.
     */
    fun setDeviceIdentifier(deviceIdentifier: Device.Identifier?) {
        this.deviceIdentifier.value = deviceIdentifier
    }
}
