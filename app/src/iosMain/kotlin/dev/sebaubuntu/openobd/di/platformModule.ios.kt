/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.di

import dev.sebaubuntu.openobd.models.IosPlatform
import dev.sebaubuntu.openobd.models.Platform
import dev.sebaubuntu.openobd.storage.IosStorageProvider
import dev.sebaubuntu.openobd.storage.StorageProvider
import org.koin.dsl.bind
import org.koin.dsl.module

actual val platformModule = module {
    // Platform
    single { IosPlatform } bind Platform::class

    // Storage
    single { IosStorageProvider } bind StorageProvider::class
}
