/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ext

import dev.sebaubuntu.openobd.core.models.Socket
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeFully

fun ByteWriteChannel.toModel() = object : Socket.OutputStream {
    override suspend fun write(
        byteArray: ByteArray,
        offset: Int,
        length: Int,
    ) = this@toModel.writeFully(byteArray, offset, length)

    override suspend fun flush() = this@toModel.flush()
}
