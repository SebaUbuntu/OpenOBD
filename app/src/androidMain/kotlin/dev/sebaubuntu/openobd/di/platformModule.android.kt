/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.di

import dev.sebaubuntu.openobd.backend.bluetooth.AndroidBluetoothManager
import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothManager
import dev.sebaubuntu.openobd.backend.usb.AndroidUsbManager
import dev.sebaubuntu.openobd.backend.usb.UsbManager
import dev.sebaubuntu.openobd.models.AndroidPlatform
import dev.sebaubuntu.openobd.models.Platform
import dev.sebaubuntu.openobd.storage.AndroidStorageProvider
import dev.sebaubuntu.openobd.storage.StorageProvider
import dev.sebaubuntu.openobd.utils.AndroidPermissionsManager
import dev.sebaubuntu.openobd.utils.PermissionsManager
import org.koin.androidx.scope.dsl.activityScope
import org.koin.core.module.dsl.scopedOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    // Platform
    singleOf(::AndroidPlatform) bind Platform::class

    // Storage
    singleOf(::AndroidStorageProvider) bind StorageProvider::class

    // Device managers
    singleOf(::AndroidBluetoothManager) bind BluetoothManager::class
    singleOf(::AndroidUsbManager) bind UsbManager::class

    // Permissions manager
    activityScope {
        scopedOf(::AndroidPermissionsManager) bind PermissionsManager::class
    }
}
