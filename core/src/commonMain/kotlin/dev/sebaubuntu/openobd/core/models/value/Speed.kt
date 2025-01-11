/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Speed.
 */
class Speed(
    value: Number,
    unit: Unit,
) : Value<Number, Speed.Unit, Speed>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, Speed> {
        METER_PER_SECOND("m/s"),
        MILE_PER_HOUR("mph"),
        KILOMETER_PER_HOUR("km/h"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.METER_PER_SECOND -> when (unit) {
            Unit.METER_PER_SECOND -> this
            Unit.MILE_PER_HOUR -> Speed(value * MPS_TO_MPH, unit)
            Unit.KILOMETER_PER_HOUR -> Speed(value * MPS_TO_KMH, unit)
        }

        Unit.MILE_PER_HOUR -> when (unit) {
            Unit.METER_PER_SECOND -> Speed(value / MPS_TO_MPH, unit)
            Unit.MILE_PER_HOUR -> this
            Unit.KILOMETER_PER_HOUR -> Speed(value * MPH_TO_KMH, unit)
        }

        Unit.KILOMETER_PER_HOUR -> when (unit) {
            Unit.METER_PER_SECOND -> Speed(value / MPS_TO_KMH, unit)
            Unit.MILE_PER_HOUR -> Speed(value / MPH_TO_KMH, unit)
            Unit.KILOMETER_PER_HOUR -> this
        }
    }

    companion object : Converter<Number, Unit, Speed>(::Speed) {
        private const val MPS_TO_MPH = 2.237f
        private const val MPS_TO_KMH = 3.6f
        private const val MPH_TO_KMH = 1.609f

        /**
         * @see Unit.METER_PER_SECOND
         */
        val Number.metersPerSecond by valueProperty(Unit.METER_PER_SECOND)

        /**
         * @see Unit.KILOMETER_PER_HOUR
         */
        val Number.kilometersPerHour by valueProperty(Unit.KILOMETER_PER_HOUR)
    }
}
