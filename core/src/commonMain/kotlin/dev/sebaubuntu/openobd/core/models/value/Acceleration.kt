/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Acceleration.
 */
class Acceleration(
    value: Number,
    unit: Unit,
) : Value<Number, Acceleration.Unit, Acceleration>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, Acceleration> {
        METER_PER_SECOND_SQUARED("m/s²"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.METER_PER_SECOND_SQUARED -> when (unit) {
            Unit.METER_PER_SECOND_SQUARED -> this
        }
    }

    companion object : Converter<Number, Unit, Acceleration>(::Acceleration) {
        /**
         * @see Unit.METER_PER_SECOND_SQUARED
         */
        val Number.metersPerSecondSquared by valueProperty(Unit.METER_PER_SECOND_SQUARED)
    }
}
