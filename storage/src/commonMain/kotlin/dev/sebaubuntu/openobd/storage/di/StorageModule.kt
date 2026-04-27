/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.storage.di

import dev.sebaubuntu.openobd.storage.StorageProvider
import dev.sebaubuntu.openobd.storage.database.AppDatabase
import dev.sebaubuntu.openobd.storage.preferences.PreferencesManager
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module(
    includes = [
        PlatformStorageModule::class,
    ],
)
@Configuration
class StorageModule {
    @Single
    fun providesPreferencesManager(
        storageProvider: StorageProvider,
    ): PreferencesManager = PreferencesManager.get(storageProvider)

    @Single
    fun providesAppDatabase(
        storageProvider: StorageProvider,
    ): AppDatabase = AppDatabase.get(storageProvider)
}
