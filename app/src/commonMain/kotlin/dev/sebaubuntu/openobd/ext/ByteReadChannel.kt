/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ext

import dev.sebaubuntu.openobd.core.models.Socket
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.availableForRead
import io.ktor.utils.io.discard
import io.ktor.utils.io.readAvailable

fun ByteReadChannel.toModel() = object : Socket.InputStream {
    override fun available() = this@toModel.availableForRead

    override suspend fun read(
        byteArray: ByteArray,
        offset: Int,
        length: Int,
    ) = this@toModel.readAvailable(byteArray, offset, length)

    override suspend fun skip(n: Long) = this@toModel.discard(n)
}
