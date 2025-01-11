/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
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
    enum class Unit : Value.Unit<Double, Unit, Angle> {
        DEGREE,
        RADIAN,
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
}
