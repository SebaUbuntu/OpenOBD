/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import dev.sebaubuntu.openobd.app.models.MeasurementSystem
import dev.sebaubuntu.openobd.app.models.Platform
import dev.sebaubuntu.openobd.app.models.Theme
import dev.sebaubuntu.openobd.core.models.value.Speed
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
    platform: Platform,
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
        when (platform.measurementSystem) {
            MeasurementSystem.METRIC -> Speed.Unit.KILOMETER_PER_HOUR
            MeasurementSystem.IMPERIAL_US -> Speed.Unit.MILE_PER_HOUR
            MeasurementSystem.IMPERIAL_UK -> Speed.Unit.MILE_PER_HOUR
        },
    ).asPreferenceHolder()

    val temperatureUnit = enumPreference(
        "temperature_unit",
        platform.defaultTemperatureUnit,
    ).asPreferenceHolder()

    // Profile

    val profileId = primitivePreference(
        "profile_id",
        Profile.DEFAULT.id,
    ).asPreferenceHolder()

    private fun <T> Preference<T>.asPreferenceHolder() = PreferenceHolder(this)
}
