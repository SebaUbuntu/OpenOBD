/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.storage

import androidx.room.Room
import dev.sebaubuntu.openobd.storage.database.AppDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

object IosStorageProvider : StorageProvider {
    override fun getDatabaseBuilder(filename: String) = Room.databaseBuilder<AppDatabase>(
        name = getFilePath(filename),
    )

    override fun getDataStoreFile(filename: String) = getFilePath(filename)

    @OptIn(ExperimentalForeignApi::class)
    private fun getFilePath(filename: String) = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )?.path?.let { "$it/$filename" } ?: error("Could not get the document directory")
}
