/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.models

import kotlinx.io.IOException
import kotlinx.io.RawSink
import kotlinx.io.RawSource

/**
 * A raw socket.
 */
interface RawSocket : RawSource, RawSink {
    /**
     * Closes this socket and releases the resources held by this socket. It is an error to
     * read/write a closed socket. It is safe to close a socket more than once.
     *
     * @throws IOException when some I/O error occurs.
     */
    override fun close()

    companion object {
        fun RawSocket(
            source: RawSource,
            sink: RawSink,
        ): RawSocket = object : RawSocket, RawSource by source, RawSink by sink {
            override fun close() {
                source.close()
                sink.close()
            }
        }
    }
}
