/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.core

/**
 * A protocol transmitter.
 */
interface Transmitter<F : Frame> : Interface<F> {
    /**
     * Send a frame.
     *
     * @param frame The frame to send
     */
    suspend fun transmit(frame: F)
}
