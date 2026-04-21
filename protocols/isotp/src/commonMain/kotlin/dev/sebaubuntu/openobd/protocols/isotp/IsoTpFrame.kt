/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.isotp

import dev.sebaubuntu.openobd.protocols.core.Frame

/**
 * ISO-TP frame.
 */
interface IsoTpFrame : Frame {
    /**
     * Single frame. There will be only this frame in a message.
     *
     * @param data The data of the single frame
     */
    data class Single(
        val data: List<UByte>,
    ) : IsoTpFrame

    /**
     * First frame of a multi-frame message.
     *
     * @param totalBytes The total number of bytes in the message
     * @param data The data of the first frame
     */
    data class First(
        val totalBytes: UInt,
        val data: List<UByte>,
    ) : IsoTpFrame

    /**
     * Subsequent frame of a multi-frame message.
     *
     * @param index The index of this frame in the message, starting from 0
     */
    data class Consecutive(
        val index: UShort,
        val data: List<UByte>,
    ) : IsoTpFrame

    /**
     * Flow control frame.
     */
    sealed interface FlowControl : IsoTpFrame {
        /**
         * Flow status.
         */
        enum class FlowStatus(val value: UByte) {
            /**
             * Continue to send.
             */
            CTS(0x0u),

            /**
             * Wait.
             */
            WAIT(0x1u),

            /**
             * Overflow / abort.
             */
            STOP(0x2u),
        }

        /**
         * The flow status.
         */
        val flowStatus: FlowStatus

        /**
         * Number of frames to send before sending a flow control frame.
         *
         * 0 to disable flow control.
         */
        val blockSize: UByte

        /**
         * Time to wait before sending the next data frame.
         *
         * The meaning depends on [blockSize] value.
         */
        val separationTime: UByte

        /**
         * Flow control frame.
         *
         * @param separationTimeMs Time to wait before sending the next data frame in milliseconds
         */
        data class WithoutFlowControl(
            override val flowStatus: FlowStatus,
            val separationTimeMs: Int,
        ) : FlowControl {
            init {
                require(separationTimeMs in allowedSeparationTimeRange) {
                    "Separation time must be in $allowedSeparationTimeRange ms"
                }
            }

            override val blockSize: UByte = 0x0u

            override val separationTime = separationTimeMs.toUByte()

            companion object {
                private val allowedSeparationTimeRange = 0..127
            }
        }

        /**
         * Flow control frame.
         *
         * @param separationTimeUs Time to wait before sending the next data frame in microseconds
         */
        data class WithFlowControl(
            override val flowStatus: FlowStatus,
            override val blockSize: UByte,
            val separationTimeUs: Int,
        ) : FlowControl {
            init {
                require(separationTimeUs in allowedSeparationTimeRange) {
                    "Separation time must be in $allowedSeparationTimeRange μs"
                }
            }

            override val separationTime = 0xF0u.toUByte() and (separationTimeUs / 100).toUByte()

            companion object {
                private val allowedSeparationTimeRange = 100..900
            }
        }
    }
}
