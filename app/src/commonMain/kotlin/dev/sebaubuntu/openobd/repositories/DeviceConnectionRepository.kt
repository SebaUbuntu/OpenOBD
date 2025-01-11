/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.models.DeviceType
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.mapLatestData
import dev.sebaubuntu.openobd.core.models.Result
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
import kotlinx.coroutines.flow.stateIn

class DeviceConnectionRepository(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val bluetoothRepository: BluetoothRepository,
    private val networkRepository: NetworkRepository,
) {
    private val deviceIdentifier = MutableStateFlow<Pair<DeviceType, String>?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val deviceConnection = deviceIdentifier
        .flatMapLatest { deviceIdentifier ->
            deviceIdentifier?.let {
                val (connectionType, deviceId) = it

                when (connectionType) {
                    DeviceType.BLUETOOTH -> bluetoothRepository.connect(deviceId)
                    DeviceType.NETWORK -> networkRepository.connect(deviceId)
                    else -> TODO()
                }
            } ?: flowOf(Result.Error(Error.NOT_FOUND))
        }
        .asFlowResult()
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    /**
     * Connected device information.
     */
    val device = deviceConnection
        .mapLatestData { it.device }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    /**
     * Current socket.
     */
    val socket = deviceConnection
        .mapLatestData { it.socket }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    /**
     * Set the target device's identifier.
     */
    fun setDeviceIdentifier(deviceType: DeviceType, deviceId: String) {
        deviceIdentifier.value = deviceType to deviceId
    }

    /**
     * Disconnect from the target device.
     */
    fun disconnect() {
        deviceIdentifier.value = null
    }
}
