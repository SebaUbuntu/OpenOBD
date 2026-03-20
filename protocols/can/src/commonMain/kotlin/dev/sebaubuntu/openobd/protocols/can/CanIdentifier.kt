/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.can

/**
 * CAN identifier.
 */
sealed interface CanIdentifier {
    /**
     * 11-bit CAN identifier.
     */
    value class Standard(val id: UShort) : CanIdentifier

    /**
     * 29-bit CAN identifier.
     */
    value class Extended(val id: UInt) : CanIdentifier

    companion object {
        /**
         * Get the smallest [CanIdentifier] that can represent the given [id].
         *
         * @param id The identifier value
         * @return The smallest [CanIdentifier] that can represent the given [id]
         */
        fun smallestFor(id: UInt) = when {
            id < 0x1000u -> Standard(id.toUShort())
            else -> Extended(id)
        }
    }
}
