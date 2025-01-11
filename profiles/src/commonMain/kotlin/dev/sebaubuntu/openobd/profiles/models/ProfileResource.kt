/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.profiles.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Profile resources.
 *
 * @param filename The filename of the resource
 */
@Serializable
enum class ProfileResource(
    val filename: String,
) {
    @SerialName("diagnostic_trouble_codes")
    DIAGNOSTIC_TROUBLE_CODES("diagnostic_trouble_codes.json")
}
