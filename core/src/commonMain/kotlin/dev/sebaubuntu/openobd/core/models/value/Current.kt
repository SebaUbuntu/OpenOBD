/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Current.
 */
class Current(
    value: Number,
    unit: Unit,
) : Value<Number, Current.Unit, Current>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, Current> {
        AMPERE("A"),
        MILLIAMPERE("mA"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.AMPERE -> when (unit) {
            Unit.AMPERE -> this
            Unit.MILLIAMPERE -> Current(value * 1000, unit)
        }

        Unit.MILLIAMPERE -> when (unit) {
            Unit.AMPERE -> Current(value / 1000f, unit)
            Unit.MILLIAMPERE -> this
        }
    }

    companion object : Converter<Number, Unit, Current>(::Current) {
        /**
         * @see Unit.AMPERE
         */
        val Number.amperes by valueProperty(Unit.AMPERE)

        /**
         * @see Unit.MILLIAMPERE
         */
        val Number.milliamperes by valueProperty(Unit.MILLIAMPERE)
    }
}
