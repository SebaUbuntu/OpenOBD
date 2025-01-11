/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.elm327.models

/**
 * ELM327 session info.
 *
 * @param obdProtocol The OBD protocol
 * @param obdProtocolDescription The description of the OBD protocol
 */
data class SessionInfo(
    val obdProtocol: Pair<Boolean, ObdProtocol>?,
    val obdProtocolDescription: String?,
)
