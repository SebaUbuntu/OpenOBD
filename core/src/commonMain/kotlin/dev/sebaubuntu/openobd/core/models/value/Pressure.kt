/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Pressure.
 */
class Pressure(
    value: Float,
    unit: Unit,
) : Value<Float, Pressure.Unit, Pressure>(value, unit) {
    enum class Unit : Value.Unit<Float, Unit, Pressure> {
        PASCAL,
        KILOPASCAL,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.PASCAL -> when (unit) {
            Unit.PASCAL -> this
            Unit.KILOPASCAL -> Pressure(value / 1000, unit)
        }

        Unit.KILOPASCAL -> when (unit) {
            Unit.PASCAL -> Pressure(value * 1000, unit)
            Unit.KILOPASCAL -> this
        }
    }
}
