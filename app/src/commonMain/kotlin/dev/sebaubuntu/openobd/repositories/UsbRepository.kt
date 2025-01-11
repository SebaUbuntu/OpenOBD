/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.models.UsbDevice
import dev.sebaubuntu.openobd.backend.usb.UsbManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn

/**
 * USB repository.
 */
class UsbRepository(
    usbManager: UsbManager,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : BaseDeviceRepository<UsbDevice, UsbDevice.Identifier>(
    usbManager, coroutineScope, coroutineDispatcher
) {
    val devices = usbManager.devices()
        .flowOn(coroutineDispatcher)
        .shareIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )
}
