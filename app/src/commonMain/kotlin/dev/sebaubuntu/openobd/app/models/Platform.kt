/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.models

import dev.sebaubuntu.openobd.core.models.value.Temperature
import kotlinx.coroutines.flow.Flow

/**
 * Platform information.
 */
interface Platform {
    /**
     * Device type.
     */
    enum class DeviceType {
        DESKTOP,
        MOBILE,
        TV,
        WATCH,
        CAR,
    }

    /**
     * Get a list of non-localized information that can be used to identify the platform.
     */
    fun information(): Flow<List<String>>

    /**
     * Get the [DeviceType].
     */
    val deviceType: DeviceType

    /**
     * Get the [MeasurementSystem] to use based on either user choice or the locale.
     */
    val measurementSystem: MeasurementSystem

    /**
     * Get the default temperature unit based on either user choice or the locale.
     */
    val defaultTemperatureUnit: Temperature.Unit
}
