/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.utils

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.logging.Logger
import kotlin.reflect.safeCast

/**
 * ISO-TP (or ISO 15765-2) messages decoder.
 *
 * @see <a href="https://en.wikipedia.org/wiki/ISO_15765-2">ISO 15765-2</a>
 */
@OptIn(ExperimentalUnsignedTypes::class)
object IsoTpDecoder {
    private val LOG_TAG = IsoTpDecoder::class.simpleName!!

    /**
     * A ISO-TP frame.
     */
    sealed interface Frame {
        /**
         * The contained data.
         */
        val data: UByteArray

        /**
         * Single frame. There will be only this frame in a message.
         */
        data class Single(
            override val data: UByteArray,
        ) : Frame

        /**
         * First frame of many to come.
         *
         * @param totalBytes The total number of bytes in the message
         * @see Consecutive
         */
        data class First(
            override val data: UByteArray,
            val totalBytes: Int,
        ) : Frame

        /**
         * Consecutive frame.
         *
         * @param index The index of this frame in the message, starting from 0, excluding the
         *   [First] message
         */
        data class Consecutive(
            override val data: UByteArray,
            val index: Int,
        ) : Frame
    }

    /**
     * Parse a ISO-TP message.
     *
     * @param data The data to parse
     * @param frameSizeBytes The size of each frame in bytes
     */
    fun parse(
        data: UByteArray,
        frameSizeBytes: Int,
    ): Result<UByteArray, Error> {
        if (data.isEmpty()) {
            Logger.error(LOG_TAG) { "ISO-TP requires at least one frame" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        val frames = mutableListOf<Frame>()

        data.chunked(frameSizeBytes).forEach { frame ->
            when {
                frame[0] in 0x00.toUByte()..0x0Fu.toUByte() -> {
                    // Single frame
                    val size = frame[0].and(0x0Fu)
                    if (size > 7u) {
                        Logger.error(LOG_TAG) { "Invalid frame size: $size" }
                        return Result.Error(Error.INVALID_RESPONSE)
                    }

                    if (frame.size != size.toInt() + 1) {
                        Logger.error(LOG_TAG) {
                            "Invalid frame size: ${frame.size} != ${size.toInt() + 1}"
                        }
                        return Result.Error(Error.INVALID_RESPONSE)
                    }

                    frames.add(
                        Frame.Single(
                            data = frame.drop(1).take(size.toInt()).toUByteArray(),
                        )
                    )
                }

                frame[0] in 0x10u.toUByte()..0x1Fu.toUByte() -> {
                    // First frame
                    val totalBytes = frame[0].toUInt().and(0x0Fu).shl(8)
                        .or(frame[1].toUInt())
                    if (totalBytes !in 8u..4095u) {
                        Logger.error(LOG_TAG) { "Invalid total bytes: $totalBytes" }
                        return Result.Error(Error.INVALID_RESPONSE)
                    }

                    frames.add(
                        Frame.First(
                            data = frame.drop(2).toUByteArray(),
                            totalBytes = totalBytes.toInt(),
                        )
                    )
                }

                frame[0] in 0x20u.toUByte()..0x2Fu.toUByte() -> {
                    // Consecutive frame
                    val index = frame[0].toUInt().and(0x0Fu)

                    frames.add(
                        Frame.Consecutive(
                            data = frame.drop(1).toUByteArray(),
                            index = index.toInt(),
                        )
                    )
                }

                else -> {
                    Logger.error(LOG_TAG) { "Invalid frame type: ${frame[0]}" }
                    return Result.Error(Error.INVALID_RESPONSE)
                }
            }
        }

        // Sort the frames by type and index
        frames.sortBy { frame ->
            when (frame) {
                is Frame.Single -> 0
                is Frame.First -> 1
                is Frame.Consecutive -> frame.index + 2
            }
        }

        val bytes = mutableListOf<UByte>()
        var totalBytes = -1

        when (frames.size) {
            0 -> {
                Logger.error(LOG_TAG) { "ISO-TP requires at least one frame" }
                return Result.Error(Error.INVALID_RESPONSE)
            }

            1 -> {
                // Must be a single frame
                Frame.Single::class.safeCast(frames.first())?.let {
                    bytes.addAll(it.data)
                    totalBytes = it.data.size
                } ?: run {
                    Logger.error(LOG_TAG) { "First frame is not a single frame" }
                    return Result.Error(Error.INVALID_RESPONSE)
                }
            }

            else -> {
                // 0: First, >= 1: Consecutive
                var lastIndex = -1

                frames.forEach { frame ->
                    when (frame) {
                        is Frame.Single -> {
                            Logger.error(LOG_TAG) {
                                "Got single frame in message with multiple frames"
                            }
                            return Result.Error(Error.INVALID_RESPONSE)
                        }

                        is Frame.First -> {
                            if (lastIndex != -1) {
                                Logger.error(LOG_TAG) { "Got multiple first frames in message" }
                                return Result.Error(Error.INVALID_RESPONSE)
                            }
                            lastIndex = 0

                            totalBytes = frame.totalBytes
                        }

                        is Frame.Consecutive -> {
                            if (lastIndex != frame.index) {
                                Logger.error { "Got unexpected frame index: ${frame.index}" }
                                return Result.Error(Error.INVALID_RESPONSE)
                            }
                            lastIndex = frame.index + 1
                        }
                    }

                    bytes.addAll(frame.data)
                }
            }
        }

        // Check if we have a valid number of bytes
        if (totalBytes == -1) {
            Logger.error(LOG_TAG) { "Total bytes not set, missing first frame?" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        // Check if we have the correct number of bytes
        if (bytes.size < totalBytes) {
            Logger.error(LOG_TAG) { "Invalid number of bytes: ${bytes.size} < $totalBytes" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        return Result.Success(bytes.take(totalBytes).toUByteArray())
    }
}
