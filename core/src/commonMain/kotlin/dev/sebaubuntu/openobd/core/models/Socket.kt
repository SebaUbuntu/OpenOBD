/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models

/**
 * A socket.
 */
interface Socket {
    val inputStream: InputStream
    val outputStream: OutputStream

    /**
     * A stream where you can read data from.
     */
    interface InputStream {
        /**
         * Returns the number of bytes available for reading.
         */
        fun available(): Int

        /**
         * Read data from the socket.
         *
         * @param byteArray The buffer to read into
         * @param offset The offset to read from
         * @param length The maximum number of bytes to read
         * @return The number of bytes read
         */
        suspend fun read(byteArray: ByteArray, offset: Int = 0, length: Int = byteArray.size): Int

        /**
         * Skip a number of bytes.
         *
         * @param n The number of bytes to skip
         * @return The number of actual skipped bytes
         */
        suspend fun skip(n: Long): Long

        /**
         * Flush all bytes in the input stream.
         *
         * @return The number of bytes flushed
         */
        suspend fun flush(): Long {
            var bytesFlushed = 0L

            while (true) {
                val available = available()
                if (available > 0) {
                    bytesFlushed += skip(available.toLong())
                } else {
                    break
                }
            }

            return bytesFlushed
        }
    }

    /**
     * A stream where you can write data to.
     */
    interface OutputStream {
        /**
         * Write data to the socket.
         *
         * @param byteArray The data to write
         * @param offset The offset of the data to write
         * @param length The length of the data to write
         */
        suspend fun write(byteArray: ByteArray, offset: Int = 0, length: Int = byteArray.size)

        /**
         * Flush all bytes in the output stream.
         */
        suspend fun flush()
    }
}
