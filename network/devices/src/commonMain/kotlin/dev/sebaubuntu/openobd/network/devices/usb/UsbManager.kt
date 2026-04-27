/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.devices.usb

import dev.sebaubuntu.openobd.network.devices.models.DeviceManager
import dev.sebaubuntu.openobd.network.devices.models.UsbDevice

/**
 * USB manager.
 */
interface UsbManager : DeviceManager<UsbDevice, UsbDevice.Identifier>
