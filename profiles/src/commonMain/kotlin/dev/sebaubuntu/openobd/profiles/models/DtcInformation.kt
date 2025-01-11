/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.profiles.models

import dev.sebaubuntu.openobd.obd2.models.DiagnosticTroubleCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [DiagnosticTroubleCode] information.
 *
 * @param name The name of the code
 * @param description A more detailed explanation of the code
 */
@Serializable
data class DtcInformation(
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String? = null,
)
