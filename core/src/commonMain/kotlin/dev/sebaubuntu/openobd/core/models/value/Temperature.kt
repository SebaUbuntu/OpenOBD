/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.minus
import dev.sebaubuntu.openobd.core.ext.plus
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Temperature.
 */
class Temperature(
    value: Number,
    unit: Unit,
) : Value<Number, Temperature.Unit, Temperature>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, Temperature> {
        CELSIUS("°C"),
        FAHRENHEIT("°F"),
        KELVIN("K"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.CELSIUS -> when (unit) {
            Unit.CELSIUS -> this
            Unit.FAHRENHEIT -> Temperature(value * 1.8f + 32, unit)
            Unit.KELVIN -> Temperature(value + 273.15f, unit)
        }

        Unit.FAHRENHEIT -> when (unit) {
            Unit.CELSIUS -> Temperature((value - 32) / 1.8f, unit)
            Unit.FAHRENHEIT -> this
            Unit.KELVIN -> Temperature((value + 459.67f) / 1.8f, unit)
        }

        Unit.KELVIN -> when (unit) {
            Unit.CELSIUS -> Temperature(value - 273.15f, unit)
            Unit.FAHRENHEIT -> Temperature(value * 1.8f - 459.67f, unit)
            Unit.KELVIN -> this
        }
    }

    companion object : Converter<Number, Unit, Temperature>(::Temperature) {
        /**
         * @see Unit.CELSIUS
         */
        val Number.celsius by valueProperty(Unit.CELSIUS)

        /**
         * @see Unit.FAHRENHEIT
         */
        val Number.fahrenheit by valueProperty(Unit.FAHRENHEIT)

        /**
         * @see Unit.KELVIN
         */
        val Number.kelvin by valueProperty(Unit.KELVIN)
    }
}
