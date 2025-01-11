/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.backend.network.NetworkManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

/**
 * Network repository.
 */
class NetworkRepository(
    networkManager: NetworkManager,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : BaseDeviceRepository<NetworkDevice, NetworkDevice.Identifier>(
    networkManager, coroutineScope, coroutineDispatcher
) {
    val devices = flowOf(networkManager.devices)
        .flowOn(coroutineDispatcher)
        .shareIn(
            coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )
}
