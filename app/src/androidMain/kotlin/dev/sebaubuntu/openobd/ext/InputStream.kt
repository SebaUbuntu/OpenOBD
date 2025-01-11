/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ext

import dev.sebaubuntu.openobd.core.models.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

fun InputStream.toModel() = object : Socket.InputStream {
    override fun available() = this@toModel.available()

    override suspend fun read(
        byteArray: ByteArray,
        offset: Int,
        length: Int,
    ) = withContext(Dispatchers.IO) {
        this@toModel.read(byteArray, offset, length)
    }

    override suspend fun skip(n: Long) = withContext(Dispatchers.IO) {
        this@toModel.skip(n)
    }
}
