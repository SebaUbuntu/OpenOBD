/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.storage

import androidx.room.RoomDatabase
import dev.sebaubuntu.openobd.storage.database.AppDatabase

interface StorageProvider {
    fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>

    fun getDataStoreFile(filename: String): String

    companion object {
        const val DATABASE_FILE_NAME = "openobd_database.db"

        const val DATASTORE_FILE_NAME = "openobd.preferences_pb"
    }
}
