/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.demo.DemoManager
import dev.sebaubuntu.openobd.backend.models.DemoDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class DemoRepository(
    deviceManager: DemoManager,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : BaseDeviceRepository<DemoManager, DemoDevice, DemoDevice.Identifier>(
    deviceManager, coroutineScope, coroutineDispatcher
)
