/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
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
    private val resources: Map<ProfileResource, ResourceStrategy>,
) {
    private val ProfileResource.strategy: ResourceStrategy
        get() = resources[this] ?: ResourceStrategy.DEFAULT

    /**
     * Gets the [DtcInformation] for each known code.
     */
    suspend fun getDiagnosticTroubleCodes() = getProfileResource(
        ProfileResource.DIAGNOSTIC_TROUBLE_CODES,
        Map<DiagnosticTroubleCode, DtcInformation>::plus,
    )

    /**
     * Get a profile resource.
     *
     * @param profileResource The [ProfileResource]
     * @param extendMethod The method to use to extend the resource
     * @param T The type of the resource
     */
    private suspend inline fun <reified T> getProfileResource(
        profileResource: ProfileResource,
        extendMethod: T.(T) -> T,
    ) = when (profileResource.strategy) {
        ResourceStrategy.DEFAULT -> readJson<T>(profileResource, true)
        ResourceStrategy.OVERRIDE -> readJson<T>(profileResource, false)
        ResourceStrategy.EXTEND -> readJson<T>(profileResource, true)
            .extendMethod(readJson<T>(profileResource, false))
    }

    /**
     * Read a JSON file.
     *
     * @param profileResource The [ProfileResource]
     * @param default Whether to read the default resource
     * @param T The type of the resource
     */
    private suspend inline fun <reified T> readJson(
        profileResource: ProfileResource,
        default: Boolean,
    ) = Json.decodeFromString<T>(readBytes(profileResource, default).decodeToString())

    /**
     * Read a resource file.
     *
     * @param profileResource The [ProfileResource]
     * @param default Whether to read the default resource
     */
    private suspend fun readBytes(
        profileResource: ProfileResource,
        default: Boolean,
    ) = Res.readBytes(getFilePath(profileResource, default))

    /**
     * Get a path to the specified resource.
     *
     * @param profileResource The [ProfileResource]
     * @param default Whether to read the default resource
     */
    private fun getFilePath(
        profileResource: ProfileResource,
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
        append(profileResource.filename)
    }

    companion object {
        private const val DEFAULT_ID = "default"

        const val PROFILES_FILES_FOLDER = "files/profiles"

        val DEFAULT = Profile(
            id = DEFAULT_ID,
            manufacturers = Manufacturer.entries.toSet(),
            displayName = "Default",
            description = "Default OBD2 profile",
            resources = mapOf(),
        )
    }
}
