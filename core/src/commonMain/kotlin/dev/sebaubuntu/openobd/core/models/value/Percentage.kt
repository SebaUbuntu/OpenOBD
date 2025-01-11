/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Percentage.
 */
class Percentage(
    value: Number,
    unit: Unit,
) : Value<Number, Percentage.Unit, Percentage>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, Percentage> {
        /**
         * 0.5.
         */
        FRACTION(null),

        /**
         * 50%.
         */
        PERCENT("%"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.FRACTION -> when (unit) {
            Unit.FRACTION -> this
            Unit.PERCENT -> value.div(100f).percent
        }

        Unit.PERCENT -> when (unit) {
            Unit.FRACTION -> value.times(100).percent
            Unit.PERCENT -> this
        }
    }

    companion object : Converter<Number, Unit, Percentage>(::Percentage) {
        /**
         * @see Unit.FRACTION
         */
        val Number.fraction by valueProperty(Unit.FRACTION)

        /**
         * @see Unit.PERCENT
         */
        val Number.percent by valueProperty(Unit.PERCENT)
    }
}
