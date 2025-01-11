/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.models

import dev.sebaubuntu.openobd.core.models.value.Voltage
import dev.sebaubuntu.openobd.elm327.models.ObdProtocol

/**
 * ELM327 session information.
 *
 * @param obdProtocol The OBD protocol
 * @param obdProtocolDescription The description of the OBD protocol
 */
data class SessionInformation(
    val deviceDescription: String?,
    val deviceIdentifier: String?,
    val versionId: String?,
    val inputVoltage: Voltage?,
    val obdProtocol: Pair<Boolean, ObdProtocol>?,
    val obdProtocolDescription: String?,
    val ignition: Boolean?,
)
