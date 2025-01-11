/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Length.
 */
class Length(
    value: Float,
    unit: Unit,
) : Value<Float, Length.Unit, Length>(value, unit) {
    enum class Unit : Value.Unit<Float, Unit, Length> {
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
