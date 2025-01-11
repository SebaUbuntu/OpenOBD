/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.profiles

import dev.sebaubuntu.openobd.profiles.models.Profile
import dev.sebaubuntu.openobd.profiles.models.Profile.Companion.DEFAULT
import dev.sebaubuntu.openobd.profiles.models.Profile.Companion.PROFILES_FILES_FOLDER
import kotlinx.serialization.json.Json
import openobd.profiles.generated.resources.Res

object Profiles {
    suspend fun fromResources() = Json.decodeFromString<List<Profile>>(
        Res.readBytes(
            "$PROFILES_FILES_FOLDER/profiles.json"
        ).decodeToString()
    ).let { profiles ->
        buildMap {
            this[DEFAULT.id] = DEFAULT

            for (profile in profiles) {
                require(contains(profile.id).not()) {
                    "Detected duplicate profile: ${profile.id}"
                }

                put(profile.id, profile)
            }
        }
    }
}
