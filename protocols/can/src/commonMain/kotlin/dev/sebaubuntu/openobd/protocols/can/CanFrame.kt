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
    val id: CanIdentifier

    /**
     * The data of the frame.
     */
    val data: List<UByte>

    /**
     * The maximum [data] length allowed by the frame type.
     */
    val maxDataSize: Int

    /**
     * Classic CAN frame.
     *
     * Data can be up to 8 bytes.
     */
    data class Classic(
        override val id: CanIdentifier,
        override val data: List<UByte>,
    ) : CanFrame {
        override val maxDataSize = 8
    }

    /**
     * CAN FD frame.
     *
     * Data can be up to 64 bytes.
     */
    data class FlexibleDataRate(
        override val id: CanIdentifier,
        override val data: List<UByte>,
    ) : CanFrame {
        override val maxDataSize = 64
    }
}
