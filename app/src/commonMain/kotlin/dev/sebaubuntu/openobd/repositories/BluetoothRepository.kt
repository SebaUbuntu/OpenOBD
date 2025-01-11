/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

/**
 * Bluetooth repository.
 */
class BluetoothRepository(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val bluetoothManager: BluetoothManager,
) {
    /**
     * @see BluetoothManager.state
     */
    val state = bluetoothManager.state()
        .flowOn(coroutineDispatcher)
        .shareIn(
            coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )

    /**
     * @see BluetoothManager.search
     */
    val devices = bluetoothManager.search()
        .flowOn(coroutineDispatcher)
        .shareIn(
            coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )

    /**
     * @see BluetoothManager.connect
     */
    fun connect(macAddress: String) = bluetoothManager.connect(macAddress)

    /**
     * @see BluetoothManager.toggle
     */
    suspend fun toggleBluetooth(isEnabled: Boolean) = bluetoothManager.toggle(isEnabled)
}
