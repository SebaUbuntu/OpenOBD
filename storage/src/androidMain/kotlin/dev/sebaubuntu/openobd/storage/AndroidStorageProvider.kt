/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.storage

import android.content.Context
import androidx.room.Room
import dev.sebaubuntu.openobd.storage.database.AppDatabase

class AndroidStorageProvider(private val context: Context) : StorageProvider {
    override fun getDatabaseBuilder() = with(context.applicationContext) {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            getDatabasePath(StorageProvider.DATABASE_FILE_NAME).absolutePath,
        )
    }

    override fun getDataStoreFile(filename: String): String {
        TODO("Not yet implemented")
    }
}
