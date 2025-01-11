/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.models.ConnectionType
import dev.sebaubuntu.openobd.models.Error
import dev.sebaubuntu.openobd.models.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

class DevicesRepository(
    coroutineScope: CoroutineScope,
    val bluetoothRepository: BluetoothRepository,
) {
    private val deviceIdentifier = MutableStateFlow<Pair<ConnectionType, String>?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val obdDevice = deviceIdentifier
        .flatMapLatest { deviceIdentifier ->
            deviceIdentifier?.let {
                val (connectionType, deviceId) = it

                when (connectionType) {
                    ConnectionType.BLUETOOTH -> bluetoothRepository.connect(deviceId)
                    else -> TODO()
                }
            } ?: MutableStateFlow(Result.Error(Error.NOT_FOUND))
        }
        .flowOn(Dispatchers.IO)
        .shareIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )

    fun setDeviceIdentifier(connectionType: ConnectionType, deviceId: String) {
        deviceIdentifier.value = connectionType to deviceId
    }
}
