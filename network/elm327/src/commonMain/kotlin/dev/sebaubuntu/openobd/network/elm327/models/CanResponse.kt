/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.elm327.models

import dev.sebaubuntu.openobd.network.can.CanIdentifier

/**
 * CAN response
 *
 * @param value The value of the response
 * @param messageFormat The message format of the response
 */
data class CanResponse<T>(
    val value: Map<CanIdentifier, T>,
    val messageFormat: MessageFormat,
) {
    enum class MessageFormat {
        /**
         * ISO 15765-4 (CAN).
         */
        ISO_TP,

        /**
         * SAE J1850.
         */
        J1850,
    }
}
