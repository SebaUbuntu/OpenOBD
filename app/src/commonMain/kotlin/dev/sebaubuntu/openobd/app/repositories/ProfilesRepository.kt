/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import dev.sebaubuntu.openobd.core.ext.flowOf
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.flatMapLatestFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.mapLatestData
import dev.sebaubuntu.openobd.profiles.Profiles
import dev.sebaubuntu.openobd.profiles.models.Manufacturer
import dev.sebaubuntu.openobd.profiles.models.Profile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

/**
 * Profiles repository.
 */
class ProfilesRepository(
    preferencesRepository: PreferencesRepository,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : Repository(coroutineScope, coroutineDispatcher) {
    private val profiles = flowOf { FlowResult.Success<_, Error>(Profiles.fromResources()) }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val activeProfile = profiles
        .flatMapLatestFlowResult { profiles ->
            preferencesRepository.profileId.mapLatest { profileId ->
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
