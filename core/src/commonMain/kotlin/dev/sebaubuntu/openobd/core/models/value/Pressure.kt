/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Pressure.
 */
class Pressure(
    value: Number,
    unit: Unit,
) : Value<Number, Pressure.Unit, Pressure>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, Pressure> {
        PASCAL("Pa"),
        KILOPASCAL("kPa"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.PASCAL -> when (unit) {
            Unit.PASCAL -> this
            Unit.KILOPASCAL -> Pressure(value / 1000f, unit)
        }

        Unit.KILOPASCAL -> when (unit) {
            Unit.PASCAL -> Pressure(value * 1000, unit)
            Unit.KILOPASCAL -> this
        }
    }

    companion object : Converter<Number, Unit, Pressure>(::Pressure) {
        /**
         * @see Unit.PASCAL
         */
        val Number.pascals by valueProperty(Unit.PASCAL)

        /**
         * @see Unit.KILOPASCAL
         */
        val Number.kilopascals by valueProperty(Unit.KILOPASCAL)
    }
}
