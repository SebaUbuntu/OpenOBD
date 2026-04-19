/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.core

import kotlinx.coroutines.flow.Flow

/**
 * A protocol receiver.
 */
interface Receiver<F : Frame> {
    /**
     * Get a flow that emits newly received frames.
     */
    fun receive(): Flow<F>
}
