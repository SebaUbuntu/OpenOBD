/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.demo.DemoManager
import dev.sebaubuntu.openobd.backend.models.DemoDevice
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

class DemoRepository(
    demoManager: DemoManager,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : BaseDeviceRepository<DemoDevice, DemoDevice.Identifier>(
    demoManager, coroutineScope, coroutineDispatcher
) {
    val devices = demoManager.devices()
        .flowOn(coroutineDispatcher)
        .shareIn(
            coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )
}
