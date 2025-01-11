/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.storage

import androidx.room.RoomDatabase
import dev.sebaubuntu.openobd.storage.database.AppDatabase

interface StorageProvider {
    fun getDatabaseBuilder(fileName: String): RoomDatabase.Builder<AppDatabase>

    fun getDataStoreFile(fileName: String): String

    companion object {
        const val DATABASE_FILE_NAME = "openobd_database.db"

        const val DATASTORE_FILE_NAME = "openobd.preferences_pb"
    }
}
