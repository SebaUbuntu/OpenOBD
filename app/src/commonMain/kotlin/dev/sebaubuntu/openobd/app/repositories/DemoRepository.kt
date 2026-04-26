/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import dev.sebaubuntu.openobd.network.devices.demo.DemoManager
import dev.sebaubuntu.openobd.network.devices.models.DemoDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

class DemoRepository(
    deviceManager: DemoManager,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : BaseDeviceRepository<DemoManager, DemoDevice, DemoDevice.Identifier>(
    deviceManager, coroutineScope, coroutineDispatcher
)
