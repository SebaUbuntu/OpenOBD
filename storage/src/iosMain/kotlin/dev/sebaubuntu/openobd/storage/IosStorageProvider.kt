/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.storage

import androidx.room.Room
import dev.sebaubuntu.openobd.storage.database.AppDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

object IosStorageProvider : StorageProvider {
    override fun getDatabaseBuilder() = Room.databaseBuilder<AppDatabase>(
        name = documentDirectory() + "/${StorageProvider.DATABASE_FILE_NAME}",
    )

    override fun getDataStoreFile(filename: String): String {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun documentDirectory() = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )?.path ?: error("Could not get the document directory")
}
