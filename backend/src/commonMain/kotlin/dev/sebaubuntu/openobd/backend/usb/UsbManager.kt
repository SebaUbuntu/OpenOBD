/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.usb

import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.backend.models.StubDeviceManager
import dev.sebaubuntu.openobd.backend.models.UsbDevice

/**
 * USB manager.
 */
interface UsbManager : DeviceManager<UsbDevice, UsbDevice.Identifier> {
    companion object {
        val DEFAULT: UsbManager = object : UsbManager,
            StubDeviceManager<UsbDevice, UsbDevice.Identifier>() {}
    }
}
