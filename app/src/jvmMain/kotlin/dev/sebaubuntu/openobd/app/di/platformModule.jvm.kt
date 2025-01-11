/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.di

import dev.sebaubuntu.openobd.app.models.JvmPlatform
import dev.sebaubuntu.openobd.app.models.Platform
import dev.sebaubuntu.openobd.storage.JvmStorageProvider
import dev.sebaubuntu.openobd.storage.StorageProvider
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    // Platform
    single { JvmPlatform } bind Platform::class

    // Storage
    single { JvmStorageProvider } bind StorageProvider::class
}
