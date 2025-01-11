/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.storage.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.mutablePreferencesOf
import dev.sebaubuntu.openobd.storage.StorageProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import okio.Path.Companion.toPath

/**
 * App preferences manager.
 *
 * @param preferenceDataStore [DataStore] containing the preferences.
 */
class PreferencesManager(private val preferenceDataStore: DataStore<Preferences>) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> valueFlow(preference: Preference<T>) = preferenceDataStore.data
        .mapLatest {
            preference.getValue(it)
        }

    suspend fun <T> getValue(preference: Preference<T>) = valueFlow(preference).first()

    suspend fun <T> setValue(preference: Preference<T>, value: T) {
        preferenceDataStore.edit {
            preference.setValue(it, value)
        }
    }

    companion object {
        fun get(storageProvider: StorageProvider) = PreferencesManager(
            PreferenceDataStoreFactory.createWithPath(
                corruptionHandler = ReplaceFileCorruptionHandler { mutablePreferencesOf() },
            ) {
                storageProvider.getDataStoreFile(StorageProvider.DATASTORE_FILE_NAME).toPath()
            }
        )
    }
}
