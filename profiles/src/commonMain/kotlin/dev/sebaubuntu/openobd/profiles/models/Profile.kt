/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.profiles.models

import dev.sebaubuntu.openobd.obd2.models.DiagnosticTroubleCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import openobd.profiles.generated.resources.Res

/**
 * A connection profile. Can be used to handle non-standard responses and behaviors.
 *
 * @param id The unique identifier of the profile
 * @param manufacturers The manufacturers related to this profile
 * @param displayName A display name for this profile
 * @param description A description for this profile
 * @param resources Resource strategies
 */
@Serializable
data class Profile(
    val id: String,
    val manufacturers: Set<Manufacturer>,
    @SerialName("display_name") val displayName: String,
    val description: String? = null,
    private val resources: Map<ProfileResource, ResourceStrategy> = mapOf(),
) {
    /**
     * Gets the [DtcInformation] for each known code.
     */
    suspend fun getDiagnosticTroubleCodes() = when (
        ProfileResource.DIAGNOSTIC_TROUBLE_CODES.strategy
    ) {
        ResourceStrategy.DEFAULT -> readJson(DIAGNOSTIC_TROUBLE_CODES_FILE, true)
        ResourceStrategy.OVERRIDE -> readJson(DIAGNOSTIC_TROUBLE_CODES_FILE, false)
        ResourceStrategy.EXTEND -> readJson<Map<DiagnosticTroubleCode, DtcInformation>>(
            DIAGNOSTIC_TROUBLE_CODES_FILE, true
        ).plus(
            readJson<Map<DiagnosticTroubleCode, DtcInformation>>(
                DIAGNOSTIC_TROUBLE_CODES_FILE, false
            )
        )
    }

    private val ProfileResource.strategy: ResourceStrategy
        get() = resources[this] ?: ResourceStrategy.DEFAULT

    private suspend inline fun <reified T> readJson(
        filename: String,
        default: Boolean,
    ) = Json.decodeFromString<T>(readBytes(filename, default).decodeToString())

    private suspend fun readBytes(
        filename: String,
        default: Boolean,
    ) = Res.readBytes(getFilePath(filename, default))

    /**
     * Get a path to the specified resource.
     */
    private fun getFilePath(
        filename: String,
        default: Boolean,
    ) = buildString {
        append(PROFILES_FILES_FOLDER)
        append("/")
        append(
            when (default) {
                true -> DEFAULT_ID
                false -> id
            }
        )
        append("/")
        append(filename)
    }

    companion object {
        private const val DEFAULT_ID = "default"

        private const val DIAGNOSTIC_TROUBLE_CODES_FILE = "diagnostic_trouble_codes.json"

        const val PROFILES_FILES_FOLDER = "files/profiles"

        val DEFAULT = Profile(
            id = DEFAULT_ID,
            manufacturers = Manufacturer.entries.toSet(),
            displayName = "Default",
            description = "Default OBD2 profile",
        )
    }
}
