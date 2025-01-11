/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import dev.sebaubuntu.openobd.backend.models.UsbDevice
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.repositories.UsbRepository

class UsbDevicesViewModel(
    usbRepository: UsbRepository,
    deviceConnectionRepository: DeviceConnectionRepository,
) : BaseDevicesViewModel<UsbDevice, UsbDevice.Identifier>(
    usbRepository,
    deviceConnectionRepository,
)
