/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.flatMapLatestFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.mapLatestData
import dev.sebaubuntu.openobd.profiles.Profiles
import dev.sebaubuntu.openobd.profiles.models.Manufacturer
import dev.sebaubuntu.openobd.profiles.models.Profile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Profiles repository.
 */
class ProfilesRepository(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    settingsRepository: SettingsRepository,
) : Repository(coroutineScope, coroutineDispatcher) {
    private val _profiles = MutableStateFlow<FlowResult<Map<String, Profile>, Error>>(
        FlowResult.Loading()
    )
    private val profiles = _profiles.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeProfile = profiles
        .flatMapLatestFlowResult { profiles ->
            settingsRepository.profileId.mapLatest { profileId ->
                FlowResult.Success(profiles[profileId] ?: Profile.DEFAULT)
            }
        }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    /**
     * @see Profile.getDiagnosticTroubleCodes
     */
    val diagnosticTroubleCodes = activeProfile
        .mapLatestData { it.getDiagnosticTroubleCodes() }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    val manufacturers = Manufacturer.entries

    init {
        coroutineScope.launch(coroutineDispatcher) {
            val profiles = Profiles.fromResources()
            _profiles.emit(FlowResult.Success(profiles))
        }
    }

    /**
     * Get the profiles related to the given [Manufacturer].
     */
    fun profilesOfManufacturer(manufacturer: Manufacturer) = profiles
        .mapLatestData {
            it.values.filter { profiles ->
                profiles.manufacturers.contains(manufacturer)
            }
        }
        .flowOn(coroutineDispatcher)
}
