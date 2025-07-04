/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Speed.
 */
class Speed(
    value: Float,
    unit: Unit,
) : Value<Float, Speed.Unit, Speed>(value, unit) {
    enum class Unit : Value.Unit<Float, Unit, Speed> {
        METER_PER_SECOND,
        KILOMETER_PER_HOUR,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.METER_PER_SECOND -> when (unit) {
            Unit.METER_PER_SECOND -> this
            Unit.KILOMETER_PER_HOUR -> Speed(value * 3.6f, unit)
        }

        Unit.KILOMETER_PER_HOUR -> when (unit) {
            Unit.METER_PER_SECOND -> Speed(value / 3.6f, unit)
            Unit.KILOMETER_PER_HOUR -> this
        }
    }
}
