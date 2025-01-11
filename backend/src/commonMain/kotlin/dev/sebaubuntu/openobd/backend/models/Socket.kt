/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.models

import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.buffered

/**
 * A socket.
 */
class Socket(
    rawSource: RawSource,
    rawSink: RawSink,
) : AutoCloseable {
    val source = rawSource.buffered()
    val sink = rawSink.buffered()

    override fun close() {
        source.close()
        sink.close()
    }
}
