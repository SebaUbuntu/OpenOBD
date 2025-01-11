/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.storage.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.sebaubuntu.openobd.storage.StorageProvider
import dev.sebaubuntu.openobd.storage.database.dao.NetworkDeviceDao
import dev.sebaubuntu.openobd.storage.database.dao.VehicleDao
import dev.sebaubuntu.openobd.storage.database.entities.NetworkDevice
import dev.sebaubuntu.openobd.storage.database.entities.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [
        NetworkDevice::class,
        Vehicle::class,
    ],
    version = 1,
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun networkDeviceDao(): NetworkDeviceDao
    abstract fun vehicleDao(): VehicleDao

    companion object {
        fun get(
            storageProvider: StorageProvider,
        ) = storageProvider.getDatabaseBuilder(StorageProvider.DATABASE_FILE_NAME)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
