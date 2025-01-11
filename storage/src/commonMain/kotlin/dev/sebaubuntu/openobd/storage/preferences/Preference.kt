/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.storage.preferences

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlin.jvm.JvmName

/**
 * Preference definition.
 *
 * @param T The type of the preference
 */
sealed interface Preference<T> {
    /**
     * Get the value of the preference from the preferences.
     *
     * @param preferences The preferences to get the value from
     * @return The value of the preference, or null if it doesn't exist
     */
    fun getValue(preferences: Preferences): T

    /**
     * Update the preferences with the given value.
     *
     * @param mutablePreferences The preferences to update
     * @param value The value to set, null to remove the preference
     */
    suspend fun setValue(mutablePreferences: MutablePreferences, value: T?)

    /**
     * Preference backed by a [Preferences.Key].
     *
     * @param T The type of the preference
     * @param BT The type of the backing preference
     */
    sealed class BackingPreference<T, BT : Any>(
        private val preferencesKey: Preferences.Key<BT>,
        private val defaultValue: T,
    ) : Preference<T> {
        protected abstract fun backingValueToValue(backingValue: BT): T
        protected abstract fun valueToBackingValue(value: T & Any): BT

        override fun getValue(
            preferences: Preferences,
        ) = preferences[preferencesKey]?.let(::backingValueToValue) ?: defaultValue

        override suspend fun setValue(mutablePreferences: MutablePreferences, value: T?) {
            value?.also {
                mutablePreferences[preferencesKey] = valueToBackingValue(it)
            } ?: mutablePreferences.remove(preferencesKey)
        }
    }

    /**
     * Direct access to a primitive preference.
     */
    sealed class PrimitivePreference<T>(
        preferencesKey: Preferences.Key<T & Any>,
        defaultValue: T,
    ) : BackingPreference<T, T & Any>(preferencesKey, defaultValue) {
        override fun backingValueToValue(backingValue: T & Any) = backingValue
        override fun valueToBackingValue(value: T & Any) = value
    }

    /**
     * Boolean preference.
     */
    class BooleanPreference<T : Boolean?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        booleanPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * Byte array preference.
     */
    class ByteArrayPreference<T : ByteArray?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        byteArrayPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * Double preference.
     */
    class DoublePreference<T : Double?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        doublePreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * Float preference.
     */
    class FloatPreference<T : Float?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        floatPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * Int preference.
     */
    class IntPreference<T : Int?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        intPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * Long preference.
     */
    class LongPreference<T : Long?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        longPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * String preference.
     */
    class StringPreference<T : String?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        stringPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * String set preference.
     */
    class StringSetPreference<T : Set<String>?>(
        name: String,
        defaultValue: T,
    ) : PrimitivePreference<T>(
        stringSetPreferencesKey(name).forceCast(),
        defaultValue,
    )

    /**
     * Enum preference.
     */
    class EnumPreference<T : Enum<T & Any>?>(
        name: String,
        defaultValue: T,
        private val enumValueOf: (String) -> T,
    ) : BackingPreference<T, String>(
        stringPreferencesKey(name),
        defaultValue,
    ) {
        override fun backingValueToValue(backingValue: String) = enumValueOf(backingValue)
        override fun valueToBackingValue(value: T & Any) = value.name
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        private fun <T> Preferences.Key<*>.forceCast() = this as Preferences.Key<T>

        fun <T : Boolean?> booleanPreference(
            name: String,
            defaultValue: T,
        ) = BooleanPreference(name, defaultValue)

        fun <T : ByteArray?> byteArrayPreference(
            name: String,
            defaultValue: T,
        ) = ByteArrayPreference(name, defaultValue)

        fun <T : Double?> doublePreference(
            name: String,
            defaultValue: T,
        ) = DoublePreference(name, defaultValue)

        fun <T : Float?> floatPreference(
            name: String,
            defaultValue: T,
        ) = FloatPreference(name, defaultValue)

        fun <T : Int?> intPreference(
            name: String,
            defaultValue: T,
        ) = IntPreference(name, defaultValue)

        fun <T : Long?> longPreference(
            name: String,
            defaultValue: T,
        ) = LongPreference(name, defaultValue)

        fun <T : String?> stringPreference(
            name: String,
            defaultValue: T,
        ) = StringPreference(name, defaultValue)

        fun <T : Set<String>?> stringSetPreference(
            name: String,
            defaultValue: T,
        ) = StringSetPreference(name, defaultValue)

        inline fun <reified T : Enum<T>> enumPreference(
            name: String,
            defaultValue: T,
        ) = EnumPreference(
            name,
            defaultValue,
            ::enumValueOf,
        )

        @JvmName("nullableEnumPreference")
        inline fun <reified T : Enum<T>> enumPreference(
            name: String,
            defaultValue: T?,
        ) = EnumPreference(
            name,
            defaultValue,
            ::enumValueOf,
        )
    }
}
