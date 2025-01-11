/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327.utils

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.logging.Logger

/**
 * ISO-TP (or ISO 15765-2) messages decoder.
 *
 * @see <a href="https://en.wikipedia.org/wiki/ISO_15765-2">ISO 15765-2</a>
 */
@OptIn(ExperimentalUnsignedTypes::class)
object IsoTpDecoder : ProtocolDecoder {
    private val LOG_TAG = IsoTpDecoder::class.simpleName!!

    override fun parseFrame(frame: UByteArray): Result<ProtocolDecoder.Frame, Error> {
        when {
            frame[0] in 0x00.toUByte()..0x0Fu.toUByte() -> {
                // Single frame
                val size = frame[0].and(0x0Fu).toInt()
                if (size > 7) {
                    Logger.error(LOG_TAG) { "Invalid frame size: $size" }
                    return Result.Error(Error.INVALID_RESPONSE)
                }

                (frame.size - 1).let { actualSize ->
                    if (size > actualSize) {
                        Logger.error(LOG_TAG) {
                            "Declared frame size ($size) > actual size ($actualSize)"
                        }
                        return Result.Error(Error.INVALID_RESPONSE)
                    } else if (size < actualSize) {
                        Logger.warn(LOG_TAG) { "Trimming single frame data: $actualSize != $size" }
                    }
                }

                return Result.Success(
                    ProtocolDecoder.Frame.Single(
                        data = frame.drop(1).take(size).toUByteArray(),
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

                return Result.Success(
                    ProtocolDecoder.Frame.First(
                        data = frame.drop(2).toUByteArray(),
                        totalBytes = totalBytes.toInt(),
                    )
                )
            }

            frame[0] in 0x20u.toUByte()..0x2Fu.toUByte() -> {
                // Consecutive frame
                val index = frame[0].toUInt().and(0x0Fu)

                return Result.Success(
                    ProtocolDecoder.Frame.Consecutive(
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
}
