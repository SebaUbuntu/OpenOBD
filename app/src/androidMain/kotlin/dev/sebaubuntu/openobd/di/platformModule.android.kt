/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.di

import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothManager
import dev.sebaubuntu.openobd.backend.usb.UsbManager
import dev.sebaubuntu.openobd.utils.AndroidBluetoothManager
import dev.sebaubuntu.openobd.utils.AndroidUsbManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    // Device managers
    singleOf(::AndroidBluetoothManager) bind BluetoothManager::class
    singleOf(::AndroidUsbManager) bind UsbManager::class
}
