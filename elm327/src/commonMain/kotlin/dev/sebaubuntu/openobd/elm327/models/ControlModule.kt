/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327.models

import kotlin.jvm.JvmInline

/**
 * Control module identification.
 *
 * @param id The ID of the control module
 */
@JvmInline
value class ControlModule(val id: UByte) : Comparable<ControlModule> {
    override fun toString() = id.toHexString(format = hexFormat)

    override fun compareTo(other: ControlModule) = compareValuesBy(this, other, ControlModule::id)

    companion object {
        private val hexFormat = HexFormat {
            number {
                prefix = "0x"
            }
            upperCase = true
        }
    }
}
