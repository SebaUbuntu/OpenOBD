/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.storage.database

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.sebaubuntu.openobd.storage.StorageProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

object AppDatabaseProvider {
    fun getDatabase(storageProvider: StorageProvider) = storageProvider.getDatabaseBuilder()
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
