/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.models

/**
 * Control module identification.
 *
 * @param id The ID of the ECU. WARNING: This should really be a nibble (4 bits), we cannot do that
 *   in Kotlin.
 */
data class ControlModule(val id: UByte) {
    init {
        require(id <= MAX_ID_VALUE) { "Invalid ID: $id" }
    }

    /**
     * ID used for OBD command requests.
     */
    val requestId = id.toUShort().or(REQUEST_MASK)

    /**
     * ID used for OBD command responses.
     */
    val responseId = id.toUShort().or(RESPONSE_MASK)

    companion object {
        private val MAX_ID_VALUE: UByte = 0xFu

        private val REQUEST_MASK: UShort = 0x07E0u

        private val RESPONSE_MASK: UShort = 0x07E8u

        /**
         * Code used to request a command to all available control modules.
         */
        const val REQUEST_ALL: UShort = 0x07DFU

        fun fromResponseId(responseId: UShort): ControlModule {
            val id = responseId.xor(RESPONSE_MASK)
            require(id <= MAX_ID_VALUE) { "Invalid ID: $id" }

            return ControlModule(id.toUByte())
        }
    }
}
