/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Mass.
 */
class Mass(
    value: Number,
    unit: Unit,
) : Value<Number, Mass.Unit, Mass>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, Mass> {
        GRAM("g"),
        KILOGRAM("kg"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.GRAM -> when (unit) {
            Unit.GRAM -> this
            Unit.KILOGRAM -> Mass(value / 1000f, unit)
        }

        Unit.KILOGRAM -> when (unit) {
            Unit.GRAM -> Mass(value * 1000, unit)
            Unit.KILOGRAM -> this
        }
    }

    companion object : Converter<Number, Unit, Mass>(::Mass) {
        /**
         * @see Unit.GRAM
         */
        val Number.grams by valueProperty(Unit.GRAM)

        /**
         * @see Unit.KILOGRAM
         */
        val Number.kilograms by valueProperty(Unit.KILOGRAM)
    }
}
