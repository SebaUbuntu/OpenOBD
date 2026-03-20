/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.core

import kotlinx.coroutines.flow.Flow

/**
 * A protocol transceiver.
 */
interface Transceiver<F : Frame> {
    /**
     * Flow that emits newly received frames.
     *
     * Once a downstream starts observing, the flow will only emit frames received after the
     * subscription.
     */
    fun receive(): Flow<F>

    /**
     * Send a frame.
     *
     * @param frame The frame to send
     */
    suspend fun transmit(frame: F)
}
