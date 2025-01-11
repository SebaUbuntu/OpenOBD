/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.models.Device
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

abstract class BaseDeviceRepository<D : Device<ID>, ID : Device.Identifier>(
    private val deviceManager: DeviceManager<D, ID>,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
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
