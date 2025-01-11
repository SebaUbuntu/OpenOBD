/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Length.
 */
class Length(
    value: Number,
    unit: Unit,
) : Value<Number, Length.Unit, Length>(value, unit) {
    enum class Unit : Value.Unit<Number, Unit, Length> {
        METER,
        KILOMETER,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.METER -> when (unit) {
            Unit.METER -> this
            Unit.KILOMETER -> Length(value / 1000, unit)
        }

        Unit.KILOMETER -> when (unit) {
            Unit.METER -> Length(value * 1000, unit)
            Unit.KILOMETER -> this
        }
    }
}
