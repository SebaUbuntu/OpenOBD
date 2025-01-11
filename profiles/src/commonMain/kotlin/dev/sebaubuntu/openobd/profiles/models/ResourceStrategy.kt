/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.profiles.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ResourceStrategy {
    /**
     * Use the default resource.
     */
    @SerialName("default")
    DEFAULT,

    /**
     * Override the default resource.
     */
    @SerialName("override")
    OVERRIDE,

    /**
     * Add or replace entries in the default resource.
     */
    @SerialName("extend")
    EXTEND,
}
