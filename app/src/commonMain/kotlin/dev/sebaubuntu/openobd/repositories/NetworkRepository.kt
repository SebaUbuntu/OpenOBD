/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.backend.network.NetworkManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

/**
 * Network repository.
 */
class NetworkRepository(
    deviceManager: NetworkManager,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : BaseDeviceRepository<NetworkManager, NetworkDevice, NetworkDevice.Identifier>(
    deviceManager, coroutineScope, coroutineDispatcher
)
