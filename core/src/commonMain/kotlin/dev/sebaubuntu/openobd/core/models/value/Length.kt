/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Length.
 */
class Length(
    value: Number,
    unit: Unit,
) : Value<Number, Length.Unit, Length>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, Length> {
        METER("m"),
        KILOMETER("km"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.METER -> when (unit) {
            Unit.METER -> this
            Unit.KILOMETER -> Length(value / 1000f, unit)
        }

        Unit.KILOMETER -> when (unit) {
            Unit.METER -> Length(value * 1000, unit)
            Unit.KILOMETER -> this
        }
    }

    companion object : Converter<Number, Unit, Length>(::Length) {
        /**
         * @see Unit.METER
         */
        val Number.meters by valueProperty(Unit.METER)

        /**
         * @see Unit.KILOMETER
         */
        val Number.kilometers by valueProperty(Unit.KILOMETER)
    }
}
