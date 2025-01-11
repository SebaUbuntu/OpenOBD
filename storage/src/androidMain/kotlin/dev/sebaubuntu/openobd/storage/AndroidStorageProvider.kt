/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.storage

import android.content.Context
import androidx.room.Room
import dev.sebaubuntu.openobd.storage.database.AppDatabase

class AndroidStorageProvider(private val context: Context) : StorageProvider {
    override fun getDatabaseBuilder(fileName: String) = with(context.applicationContext) {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            getDatabasePath(fileName).absolutePath,
        )
    }

    override fun getDataStoreFile(
        fileName: String,
    ): String = context.filesDir.resolve(fileName).absolutePath
}
