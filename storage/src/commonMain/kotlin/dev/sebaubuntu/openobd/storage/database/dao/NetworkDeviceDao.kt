/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.storage.database.dao

import androidx.room.Dao
import androidx.room.Query
import dev.sebaubuntu.openobd.storage.database.entities.NetworkDevice
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkDeviceDao {
    /**
     * Get all the devices.
     */
    @Query("SELECT * FROM NetworkDevice")
    fun getAll(): Flow<List<NetworkDevice>>

    /**
     * Get a device by its ID.
     */
    @Query("SELECT * FROM NetworkDevice WHERE networkDeviceId = :networkDeviceId")
    fun getById(networkDeviceId: Long): Flow<NetworkDevice?>

    /**
     * Create a new device.
     */
    @Query(
        """
            INSERT INTO NetworkDevice (displayName, hostname, port)
            VALUES (:displayName, :hostname, :port)
        """
    )
    suspend fun create(
        displayName: String,
        hostname: String,
        port: Int,
    )

    /**
     * Update a device.
     */
    @Query(
        """
            UPDATE NetworkDevice
            SET displayName = :displayName,
                hostname = :hostname,
                port = :port
            WHERE networkDeviceId = :networkDeviceId
        """
    )
    suspend fun update(
        networkDeviceId: Long,
        displayName: String,
        hostname: String,
        port: Int,
    )

    /**
     * Delete a device.
     */
    @Query("DELETE FROM NetworkDevice WHERE networkDeviceId = :networkDeviceId")
    suspend fun delete(networkDeviceId: Long)
}
