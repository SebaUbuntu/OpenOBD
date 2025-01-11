/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327.utils

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.logging.Logger

/**
 * Generic interface for message protocol decoders.
 */
@OptIn(ExperimentalUnsignedTypes::class)
interface ProtocolDecoder {
    /**
     * A frame.
     */
    sealed interface Frame : Comparable<Frame> {
        /**
         * The contained data.
         */
        val data: UByteArray

        override fun compareTo(other: Frame) = compareValuesBy(this, other) {
            when (this) {
                is Single -> 0
                is First -> 0
                is Consecutive -> index
            }
        }

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
         * @param index The index of this frame in the message, starting from 1, assuming 0 is the
         *   [First] message
         */
        data class Consecutive(
            override val data: UByteArray,
            val index: Int,
        ) : Frame
    }

    /**
     * Parse a frame.
     *
     * @param frame The data to parse
     */
    fun parseFrame(frame: UByteArray): Result<Frame, Error>

    /**
     * Parse a message.
     *
     * @param message The data to parse
     * @param frameSizeBytes The size of each frame in bytes
     * @return The parsed message
     */
    fun parseMessage(message: UByteArray, frameSizeBytes: Int): Result<UByteArray, Error> {
        if (message.isEmpty()) {
            Logger.error(LOG_TAG) { "At least one frame is required" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        val frames = buildList {
            message.chunked(frameSizeBytes).forEach { frame ->
                IsoTpDecoder.parseFrame(frame.toUByteArray()).getOrNull()?.also {
                    add(it)
                } ?: run {
                    Logger.error(LOG_TAG) { "Invalid frame: $frame" }
                    return Result.Error(Error.INVALID_RESPONSE)
                }
            }
        }

        return buildMessage(frames)
    }

    companion object {
        private val LOG_TAG = ProtocolDecoder::class.simpleName!!

        fun buildMessage(frames: List<Frame>): Result<UByteArray, Error> {
            // Sort the frames by type and index
            val sortedFrames = frames.sorted()

            val bytes = mutableListOf<UByte>()
            var totalBytes = -1
            when (sortedFrames.size) {
                0 -> {
                    Logger.error(LOG_TAG) { "At least one frame is required" }
                    return Result.Error(Error.INVALID_RESPONSE)
                }

                1 -> {
                    // Must be a single frame
                    when (val frame = sortedFrames.first()) {
                        is Frame.Single -> {
                            bytes.addAll(frame.data)
                            totalBytes = frame.data.size
                        }

                        else -> {
                            Logger.error(LOG_TAG) { "First frame is not a single frame" }
                            return Result.Error(Error.INVALID_RESPONSE)
                        }
                    }
                }

                else -> {
                    // 0: First, >= 1: Consecutive
                    var lastIndex = -1

                    sortedFrames.forEach { frame ->
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
                                // TODO: Will die with >16 frames messages since it wraps to 0
                                val expectedLastIndex = frame.index - 1
                                if (lastIndex != expectedLastIndex) {
                                    Logger.error(LOG_TAG) {
                                        "Got unexpected frame index: $expectedLastIndex vs $lastIndex"
                                    }
                                    return Result.Error(Error.INVALID_RESPONSE)
                                }
                                lastIndex = frame.index
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
}
