/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

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
}
