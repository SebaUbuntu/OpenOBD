/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
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
    @Query("SELECT * FROM vehicle")
    fun getAll(): Flow<List<Vehicle>>
}
