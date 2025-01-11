/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Frequency.
 */
class Frequency(
    value: Number,
    unit: Unit,
) : Value<Number, Frequency.Unit, Frequency>(value, unit) {
    enum class Unit : Value.Unit<Number, Unit, Frequency> {
        HERTZ,
        REVOLUTION_PER_MINUTE,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.HERTZ -> when (unit) {
            Unit.HERTZ -> this
            Unit.REVOLUTION_PER_MINUTE -> Frequency(value * 60, unit)
        }

        Unit.REVOLUTION_PER_MINUTE -> when (unit) {
            Unit.HERTZ -> Frequency(value / 60, unit)
            Unit.REVOLUTION_PER_MINUTE -> this
        }
    }
}
