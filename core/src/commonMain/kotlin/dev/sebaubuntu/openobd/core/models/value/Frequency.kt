/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Frequency.
 */
class Frequency(
    value: Number,
    unit: Unit,
) : Value<Number, Frequency.Unit, Frequency>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, Frequency> {
        HERTZ("Hz"),
        REVOLUTION_PER_MINUTE("rpm"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.HERTZ -> when (unit) {
            Unit.HERTZ -> this
            Unit.REVOLUTION_PER_MINUTE -> Frequency(value * 60, unit)
        }

        Unit.REVOLUTION_PER_MINUTE -> when (unit) {
            Unit.HERTZ -> Frequency(value / 60f, unit)
            Unit.REVOLUTION_PER_MINUTE -> this
        }
    }

    companion object : Converter<Number, Unit, Frequency>(::Frequency) {
        /**
         * @see Unit.HERTZ
         */
        val Number.hertz by valueProperty(Unit.HERTZ)

        /**
         * @see Unit.REVOLUTION_PER_MINUTE
         */
        val Number.revolutionsPerMinute by valueProperty(Unit.REVOLUTION_PER_MINUTE)
    }
}
