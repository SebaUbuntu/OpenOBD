/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

import dev.sebaubuntu.openobd.core.ext.getBit
import kotlin.jvm.JvmInline

/**
 * Set of supported parameter IDs.
 */
@JvmInline
value class SupportedParameterIds(
    private val parameterIds: Set<UByte>
) : Set<UByte> by parameterIds {
    override fun toString() = parameterIds.joinToString {
        it.toHexString(format = hexFormat)
    }

    companion object {
        private val hexFormat = HexFormat {
            number {
                prefix = "0x"
            }
            upperCase = true
        }

        /**
         * Creates a new [SupportedParameterIds] object from the given OBD value.
         *
         * @param value The OBD value to parse
         * @param offset The offset to add to each parameter ID
         */
        fun fromObdValue(
            value: UInt,
            offset: UByte,
        ) = SupportedParameterIds(
            mutableSetOf<UByte>().apply {
                // MSB: 0x01, LSB: 0x20
                for (i in 0 until UInt.SIZE_BITS) {
                    if (value.getBit(i)) {
                        add(
                            0x20u.minus(i.toUByte())
                                .plus(offset)
                                .toUByte()
                        )
                    }
                }
            }
        )
    }
}
