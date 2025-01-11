/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd2.models

/**
 * OBD response
 *
 * @param value The value of the response
 * @param messageFormat The message format of the response
 */
data class ObdResponse<T>(
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
