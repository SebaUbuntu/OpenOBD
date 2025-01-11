/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.profiles.models.Profile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : Repository(coroutineScope, coroutineDispatcher) {
    private val _profileId = MutableStateFlow(Profile.DEFAULT.id)
    val profileId = _profileId.asStateFlow()

    fun setProfileId(profileId: String) {
        _profileId.value = profileId
    }
}
