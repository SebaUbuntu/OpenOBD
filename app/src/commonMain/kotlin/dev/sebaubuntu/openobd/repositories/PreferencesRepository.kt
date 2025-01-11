/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.core.models.value.Speed
import dev.sebaubuntu.openobd.core.models.value.Temperature
import dev.sebaubuntu.openobd.models.Theme
import dev.sebaubuntu.openobd.profiles.models.Profile
import dev.sebaubuntu.openobd.storage.preferences.Preference
import dev.sebaubuntu.openobd.storage.preferences.Preference.Companion.enumPreference
import dev.sebaubuntu.openobd.storage.preferences.Preference.Companion.primitivePreference
import dev.sebaubuntu.openobd.storage.preferences.PreferencesManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

/**
 * User preferences repository.
 */
class PreferencesRepository(
    private val preferencesManager: PreferencesManager,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher
) : Repository(coroutineScope, coroutineDispatcher) {
    inner class PreferenceHolder<T>(
        private val preference: Preference<T>,
    ) : Flow<T> by preferencesManager.valueFlow(preference) {
        suspend fun getValue() = preferencesManager.getValue(preference)

        suspend fun setValue(value: T) = preferencesManager.setValue(preference, value)
    }

    // App theming

    val theme = enumPreference(
        "theme",
        Theme.SYSTEM,
    ).asPreferenceHolder()

    val dynamicColors = primitivePreference(
        "dynamic_colors",
        true,
    ).asPreferenceHolder()

    // Units

    val speedUnit = enumPreference(
        "speed_unit",
        Speed.Unit.KILOMETER_PER_HOUR,
    ).asPreferenceHolder()

    val temperatureUnit = enumPreference(
        "temperature_unit",
        Temperature.Unit.CELSIUS,
    ).asPreferenceHolder()

    // Profile

    val profileId = primitivePreference(
        "profile_id",
        Profile.DEFAULT.id,
    ).asPreferenceHolder()

    private fun <T> Preference<T>.asPreferenceHolder() = PreferenceHolder(this)
}
