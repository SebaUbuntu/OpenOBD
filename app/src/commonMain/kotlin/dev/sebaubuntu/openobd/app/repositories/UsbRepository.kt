/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import dev.sebaubuntu.openobd.network.devices.models.UsbDevice
import dev.sebaubuntu.openobd.network.devices.usb.UsbManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import org.koin.core.annotation.Single

/**
 * USB repository.
 */
@Single
class UsbRepository(
    deviceManager: UsbManager?,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : BaseDeviceRepository<UsbManager, UsbDevice, UsbDevice.Identifier>(
    deviceManager, coroutineScope, coroutineDispatcher
)
