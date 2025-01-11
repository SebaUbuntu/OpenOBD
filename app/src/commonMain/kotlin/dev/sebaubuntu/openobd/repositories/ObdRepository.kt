/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.getOrNull
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.mapLatestData
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.models.ConnectionType
import dev.sebaubuntu.openobd.obd.Elm327Manager
import dev.sebaubuntu.openobd.obd.commands.Command
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * OBD repository.
 */
class ObdRepository(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val bluetoothRepository: BluetoothRepository,
    private val networkRepository: NetworkRepository,
    private val elm327Manager: Elm327Manager,
) {
    private val deviceIdentifier = MutableStateFlow<Pair<ConnectionType, String>?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val deviceConnection = deviceIdentifier
        .flatMapLatest { deviceIdentifier ->
            deviceIdentifier?.let {
                val (connectionType, deviceId) = it

                when (connectionType) {
                    ConnectionType.BLUETOOTH -> bluetoothRepository.connect(deviceId)
                    ConnectionType.NETWORK -> networkRepository.connect(deviceId)
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
    private val socket = deviceConnection
        .mapLatestData { it.socket }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    init {
        coroutineScope.launch(coroutineDispatcher) {
            socket.collectLatest {
                elm327Manager.setSocket(it.getOrNull())
            }
        }
    }

    /**
     * Set the target device's identifier.
     */
    fun setDeviceIdentifier(connectionType: ConnectionType, deviceId: String) {
        deviceIdentifier.value = connectionType to deviceId
    }

    /**
     * Disconnect from the target device.
     */
    fun disconnect() {
        deviceIdentifier.value = null
    }

    // OBD operations

    /**
     * @see Elm327Manager.executeCommand
     */
    suspend fun <T> executeCommand(command: Command<T>) = elm327Manager.executeCommand(command)

    /**
     * @see Elm327Manager.pollCommand
     */
    fun <T> pollCommand(
        command: Command<T>,
        pollIntervalMs: Long,
    ) = elm327Manager.pollCommand(command, pollIntervalMs)
}
