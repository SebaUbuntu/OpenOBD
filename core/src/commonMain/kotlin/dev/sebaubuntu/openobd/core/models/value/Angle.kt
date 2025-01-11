/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.degreesToRadians
import dev.sebaubuntu.openobd.core.ext.radiansToDegrees

/**
 * Angle.
 */
class Angle(
    value: Double,
    unit: Unit,
) : Value<Double, Angle.Unit, Angle>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Double, Unit, Angle> {
        DEGREE("°"),
        RADIAN("rad"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.DEGREE -> when (unit) {
            Unit.DEGREE -> this
            Unit.RADIAN -> Angle(value.degreesToRadians(), unit)
        }

        Unit.RADIAN -> when (unit) {
            Unit.DEGREE -> Angle(value.radiansToDegrees(), unit)
            Unit.RADIAN -> this
        }
    }

    companion object : Converter<Double, Unit, Angle>(::Angle) {
        /**
         * @see Unit.DEGREE
         */
        val Double.degrees by valueProperty(Unit.DEGREE)

        /**
         * @see Unit.RADIAN
         */
        val Double.radians by valueProperty(Unit.RADIAN)
    }
}
