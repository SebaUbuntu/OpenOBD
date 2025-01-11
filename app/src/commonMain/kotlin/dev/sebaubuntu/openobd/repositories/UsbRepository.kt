/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.backend.models.UsbDevice
import dev.sebaubuntu.openobd.backend.usb.UsbManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

/**
 * USB repository.
 */
class UsbRepository(
    deviceManager: UsbManager,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : BaseDeviceRepository<UsbManager, UsbDevice, UsbDevice.Identifier>(
    deviceManager, coroutineScope, coroutineDispatcher
)
