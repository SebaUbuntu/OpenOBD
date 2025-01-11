/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.storage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Vehicle.
 *
 * @param vehicleId The vehicle ID
 * @param displayName The vehicle display name
 * @param profileId The profile ID
 */
@Entity
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val vehicleId: Long,
    val displayName: String,
    val profileId: String? = null,
)
