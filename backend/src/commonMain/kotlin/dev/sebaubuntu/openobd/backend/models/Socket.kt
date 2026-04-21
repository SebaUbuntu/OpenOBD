/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.models

import dev.sebaubuntu.openobd.core.ext.invoke
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.buffered

/**
 * A socket.
 */
class Socket(
    val source: Source,
    val sink: Sink,
) : RawSocket, RawSource by source, RawSink by sink {
    override fun close() {
        source.close()
        sink.close()
    }

    companion object {
        fun RawSocket.buffered() = Socket(
            source = this<RawSource>().buffered(),
            sink = this<RawSink>().buffered(),
        )
    }
}
