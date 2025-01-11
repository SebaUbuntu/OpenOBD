/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.models.Platform
import dev.sebaubuntu.openobd.repositories.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    preferencesRepository: PreferencesRepository,
    platform: Platform,
) : ViewModel() {
    // General
    val theme = preferencesRepository.theme
    val dynamicColors = preferencesRepository.dynamicColors

    // Units
    val speedUnit = preferencesRepository.speedUnit
    val temperatureUnit = preferencesRepository.temperatureUnit

    val platformInformation = platform.information()
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )

    fun <T> setPreferenceValue(
        preference: PreferencesRepository.PreferenceHolder<T>,
        value: T?,
    ) = viewModelScope.launch(Dispatchers.IO) {
        preference.setValue(value)
    }
}
