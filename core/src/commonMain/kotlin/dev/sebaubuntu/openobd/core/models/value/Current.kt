/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Current.
 */
class Current(
    value: Number,
    unit: Unit,
) : Value<Number, Current.Unit, Current>(value, unit) {
    enum class Unit : Value.Unit<Number, Unit, Current> {
        AMPERE,
        MILLIAMPERE,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.AMPERE -> when (unit) {
            Unit.AMPERE -> this
            Unit.MILLIAMPERE -> Current(value * 1000, unit)
        }

        Unit.MILLIAMPERE -> when (unit) {
            Unit.AMPERE -> Current(value / 1000f, unit)
            Unit.MILLIAMPERE -> this
        }
    }
}
