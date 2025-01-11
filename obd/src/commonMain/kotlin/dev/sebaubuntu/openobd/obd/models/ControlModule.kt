/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.models

import kotlin.jvm.JvmInline

/**
 * Control module identification.
 *
 * @param id The ID of the control module
 */
@JvmInline
@OptIn(ExperimentalStdlibApi::class)
value class ControlModule(val id: UByte) {
    override fun toString() = id.toHexString(format = hexFormat)

    companion object {
        private val hexFormat = HexFormat {
            number {
                prefix = "0x"
            }
            upperCase = true
        }
    }
}
