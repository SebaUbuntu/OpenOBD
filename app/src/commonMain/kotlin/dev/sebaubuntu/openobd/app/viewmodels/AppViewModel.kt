/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.app.repositories.PreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking

/**
 * View model for the App composable.
 */
class AppViewModel(
    preferencesRepository: PreferencesRepository,
) : ViewModel() {
    val theme = preferencesRepository.theme
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = runBlocking { preferencesRepository.theme.getValue() },
        )

    val dynamicColors = preferencesRepository.dynamicColors
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = runBlocking { preferencesRepository.dynamicColors.getValue() },
        )
}
