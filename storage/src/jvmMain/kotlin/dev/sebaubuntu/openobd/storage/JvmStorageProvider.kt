/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.storage

import androidx.room.Room
import dev.sebaubuntu.openobd.storage.database.AppDatabase
import java.io.File

object JvmStorageProvider : StorageProvider {
    override fun getDatabaseBuilder(fileName: String) = Room.databaseBuilder<AppDatabase>(
        name = getFilePath(fileName),
    )

    override fun getDataStoreFile(fileName: String) = getFilePath(fileName)

    private fun getFilePath(filename: String): String = File(
        System.getProperty("java.io.tmpdir"),
        filename,
    ).absolutePath
}
