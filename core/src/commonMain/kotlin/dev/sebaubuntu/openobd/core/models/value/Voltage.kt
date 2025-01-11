/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Voltage.
 */
class Voltage(
    value: Number,
    unit: Unit,
) : Value<Number, Voltage.Unit, Voltage>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, Voltage> {
        MILLIVOLT("mV"),
        VOLT("V"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.MILLIVOLT -> when (unit) {
            Unit.MILLIVOLT -> this
            Unit.VOLT -> Voltage(value / 1000f, Unit.VOLT)
        }

        Unit.VOLT -> when (unit) {
            Unit.MILLIVOLT -> Voltage(value * 1000, Unit.MILLIVOLT)
            Unit.VOLT -> this
        }
    }

    companion object : Converter<Number, Unit, Voltage>(::Voltage) {
        /**
         * @see Unit.MILLIVOLT
         */
        val Number.millivolts by valueProperty(Unit.MILLIVOLT)

        /**
         * @see Unit.VOLT
         */
        val Number.volts by valueProperty(Unit.VOLT)
    }
}
