/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.models.Device
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

abstract class BaseDeviceRepository<D : Device<ID>, ID : Device.Identifier>(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val deviceManager: DeviceManager<D, ID>,
) : Repository(coroutineScope, coroutineDispatcher) {
    /**
     * @see DeviceManager.device
     */
    fun device(identifier: ID) = deviceManager.device(identifier)

    /**
     * @see DeviceManager.connection
     */
    fun connection(identifier: ID) = deviceManager.connection(identifier)
}
