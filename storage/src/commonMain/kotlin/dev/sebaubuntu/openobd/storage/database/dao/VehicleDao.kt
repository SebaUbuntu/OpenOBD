/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.storage.database.dao

import androidx.room.Dao
import androidx.room.Query
import dev.sebaubuntu.openobd.storage.database.entities.Vehicle
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    /**
     * Get all the vehicles.
     */
    @Query("SELECT * FROM Vehicle")
    fun getAll(): Flow<List<Vehicle>>

    /**
     * Get a vehicle by its ID.
     */
    @Query("SELECT * FROM Vehicle WHERE vehicleId = :vehicleId")
    fun getById(vehicleId: Long): Flow<Vehicle?>

    /**
     * Create a new vehicle.
     */
    @Query(
        """
            INSERT INTO Vehicle (displayName, profileId)
            VALUES (:displayName, :profileId)
        """
    )
    suspend fun create(
        displayName: String,
        profileId: String? = null,
    )

    /**
     * Update a vehicle.
     */
    @Query(
        """
            UPDATE Vehicle
            SET displayName = :displayName,
                profileId = :profileId
            WHERE vehicleId = :vehicleId
        """
    )
    suspend fun update(
        vehicleId: Long,
        displayName: String,
        profileId: String? = null,
    )

    /**
     * Delete a vehicle.
     */
    @Query("DELETE FROM Vehicle WHERE vehicleId = :vehicleId")
    suspend fun delete(vehicleId: Long)
}
