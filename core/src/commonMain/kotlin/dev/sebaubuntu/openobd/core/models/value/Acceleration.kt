/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Acceleration.
 */
class Acceleration(
    value: Float,
    unit: Unit,
) : Value<Float, Acceleration.Unit, Acceleration>(value, unit) {
    enum class Unit : Value.Unit<Float, Unit, Acceleration> {
        METER_PER_SQUARED_SECOND,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.METER_PER_SQUARED_SECOND -> when (unit) {
            Unit.METER_PER_SQUARED_SECOND -> this
        }
    }
}
