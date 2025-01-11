/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.storage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Network device.
 *
 * @param networkDeviceId The network device ID
 * @param displayName The display name
 * @param hostname The hostname
 * @param port The port
 */
@Entity
data class NetworkDevice(
    @PrimaryKey(autoGenerate = true) val networkDeviceId: Long,
    val displayName: String,
    val hostname: String,
    val port: Int,
)
