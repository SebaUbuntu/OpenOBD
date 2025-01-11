/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327.models

/**
 * CAN response
 *
 * @param value The value of the response
 * @param messageFormat The message format of the response
 */
data class CanResponse<T>(
    val value: Map<ControlModule, T>,
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
