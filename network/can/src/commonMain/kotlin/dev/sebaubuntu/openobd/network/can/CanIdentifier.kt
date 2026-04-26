/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.can

import kotlin.jvm.JvmInline

/**
 * CAN identifier.
 */
sealed interface CanIdentifier {
    /**
     * 11-bit CAN identifier.
     */
    @JvmInline
    value class Standard(val value: UShort) : CanIdentifier {
        init {
            require(value in allowedValues) { "Standard CAN identifier must be in $allowedValues" }
        }

        override fun toString() = value.toHexString(format = hexFormat)

        companion object {
            private val hexFormat = HexFormat {
                number {
                    minLength = 3
                    removeLeadingZeros = true
                }

                upperCase = true
            }

            /**
             * The allowed [value] values.
             */
            val allowedValues = 0x0u..0x7FFu
        }
    }

    /**
     * 29-bit CAN identifier.
     */
    @JvmInline
    value class Extended(val value: UInt) : CanIdentifier {
        init {
            require(value in allowedValues) { "Extended CAN identifier must be in $allowedValues" }
        }

        override fun toString() = value.toHexString(format = hexFormat)

        companion object {
            private val hexFormat = HexFormat {
                number {
                    minLength = 8
                    removeLeadingZeros = true
                }

                upperCase = true
            }

            /**
             * The maximum [value] value.
             */
            val allowedValues = 0x0u..0x1FFFFFFFu
        }
    }

    companion object {
        /**
         * Get the smallest [CanIdentifier] that can represent the given [identifier].
         *
         * @param identifier The identifier value
         * @return The smallest [CanIdentifier] that can represent the given [identifier]
         */
        fun smallestFor(identifier: UInt) = when (identifier) {
            in Standard.allowedValues -> Standard(identifier.toUShort())
            in Extended.allowedValues -> Extended(identifier)
            else -> error("Invalid CAN identifier")
        }
    }
}
