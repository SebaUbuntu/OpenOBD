/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.can

import dev.sebaubuntu.openobd.protocols.core.Frame

/**
 * CAN frame.
 */
sealed interface CanFrame : Frame {
    /**
     * The ID of the frame.
     */
    val identifier: CanIdentifier

    /**
     * Classic CAN frame.
     *
     * Data can be up to 8 bytes.
     */
    sealed interface Classic : CanFrame {
        /**
         * Data frame.
         *
         * @param data The data
         */
        data class Data(
            override val identifier: CanIdentifier,
            val data: List<UByte>,
        ) : Classic {
            init {
                require(data.size <= MAX_DATA_SIZE) {
                    "Data size must be less than or equal to $MAX_DATA_SIZE"
                }
            }
        }

        /**
         * Remote request.
         *
         * @param dlc The expected reply's data length code
         */
        data class Remote(
            override val identifier: CanIdentifier,
            val dlc: UByte,
        ) : Classic

        companion object {
            /**
             * The maximum data length allowed by the frame type.
             */
            const val MAX_DATA_SIZE = 8
        }
    }

    /**
     * CAN FD frame.
     *
     * Data can be up to 64 bytes.
     */
    data class FlexibleDataRate(
        override val identifier: CanIdentifier,
        val data: List<UByte>,
    ) : CanFrame {
        init {
            require(data.size <= MAX_DATA_SIZE) {
                "Data size must be less than or equal to $MAX_DATA_SIZE"
            }
        }

        companion object {
            /**
             * The maximum [data] length allowed by the frame type.
             */
            const val MAX_DATA_SIZE = 64
        }
    }
}
