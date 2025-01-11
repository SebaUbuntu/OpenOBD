/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothManager
import dev.sebaubuntu.openobd.backend.models.BluetoothDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

/**
 * Bluetooth repository.
 */
class BluetoothRepository(
    private val bluetoothManager: BluetoothManager,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : BaseDeviceRepository<BluetoothDevice, BluetoothDevice.Identifier>(
    bluetoothManager, coroutineScope, coroutineDispatcher
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
     * @see BluetoothManager.toggle
     */
    suspend fun toggleBluetooth(isEnabled: Boolean) = bluetoothManager.toggle(isEnabled)
}
