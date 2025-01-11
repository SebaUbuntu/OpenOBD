/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.storage

import androidx.room.Room
import dev.sebaubuntu.openobd.storage.database.AppDatabase
import java.io.File

object JvmStorageProvider : StorageProvider {
    override fun getDatabaseBuilder() = Room.databaseBuilder<AppDatabase>(
        name = File(
            System.getProperty("java.io.tmpdir"),
            StorageProvider.DATABASE_FILE_NAME,
        ).absolutePath,
    )

    override fun getDataStoreFile(filename: String): String {
        TODO("Not yet implemented")
    }
}
