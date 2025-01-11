/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Temperature.
 */
class Temperature(
    value: Float,
    unit: Unit,
) : Value<Float, Temperature.Unit, Temperature>(value, unit) {
    enum class Unit : Value.Unit<Float, Unit, Temperature> {
        CELSIUS,
        FAHRENHEIT,
        KELVIN,
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
}
