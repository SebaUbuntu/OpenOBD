/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Power.
 */
class Power(
    value: Number,
    unit: Unit,
) : Value<Number, Power.Unit, Power>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, Power> {
        WATT("W"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.WATT -> when (unit) {
            Unit.WATT -> this
        }
    }

    companion object : Converter<Number, Unit, Power>(::Power) {
        /**
         * @see Unit.WATT
         */
        val Number.watts by valueProperty(Unit.WATT)
    }
}
