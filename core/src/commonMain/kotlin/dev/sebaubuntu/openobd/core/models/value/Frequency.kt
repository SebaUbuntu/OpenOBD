/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Frequency.
 */
class Frequency(
    value: Float,
    unit: Unit,
) : Value<Float, Frequency.Unit, Frequency>(value, unit) {
    enum class Unit : Value.Unit<Float, Unit, Frequency> {
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
