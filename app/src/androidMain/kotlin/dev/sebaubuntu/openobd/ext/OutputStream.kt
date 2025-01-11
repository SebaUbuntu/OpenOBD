/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ext

import dev.sebaubuntu.openobd.core.models.Socket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.OutputStream

fun OutputStream.toModel() = object : Socket.OutputStream {
    override suspend fun write(
        byteArray: ByteArray,
        offset: Int,
        length: Int,
    ) = withContext(Dispatchers.IO) {
        this@toModel.write(byteArray, offset, length)
    }

    override suspend fun flush() = withContext(Dispatchers.IO) {
        this@toModel.flush()
    }
}
