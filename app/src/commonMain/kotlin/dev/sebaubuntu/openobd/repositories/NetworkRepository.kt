/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.network.NetworkDevice
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.backend.network.NetworkManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

/**
 * Network repository.
 */
class NetworkRepository(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val networkManager: NetworkManager,
) {
    private val _devices = mutableMapOf<String, NetworkDevice>().apply {
        this["0"] = NetworkDevice(
            "ELM327-emulator",
            "127.0.0.1",
            35000,
        )
    }

    val devices = flowOf(_devices)
        .flowOn(coroutineDispatcher)
        .shareIn(
            coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )

    fun connect(deviceId: String) = _devices[deviceId]?.let { networkDevice ->
        networkManager.connect(networkDevice)
    } ?: flowOf(Result.Error(Error.NOT_FOUND))
}
