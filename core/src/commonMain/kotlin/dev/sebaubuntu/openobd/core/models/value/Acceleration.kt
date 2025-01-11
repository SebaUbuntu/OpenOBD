/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Acceleration.
 */
class Acceleration(
    value: Number,
    unit: Unit,
) : Value<Number, Acceleration.Unit, Acceleration>(value, unit) {
    enum class Unit : Value.Unit<Number, Unit, Acceleration> {
        METER_PER_SQUARED_SECOND,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.METER_PER_SQUARED_SECOND -> when (unit) {
            Unit.METER_PER_SQUARED_SECOND -> this
        }
    }
}
