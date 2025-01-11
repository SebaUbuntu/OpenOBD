/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import dev.sebaubuntu.openobd.app.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.app.repositories.UsbRepository
import dev.sebaubuntu.openobd.backend.models.UsbDevice

class UsbDevicesViewModel(
    usbRepository: UsbRepository,
    deviceConnectionRepository: DeviceConnectionRepository,
) : BaseDevicesViewModel<UsbDevice, UsbDevice.Identifier>(
    usbRepository,
    deviceConnectionRepository,
)
