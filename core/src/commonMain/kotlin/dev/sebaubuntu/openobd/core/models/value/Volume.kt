/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Volume.
 */
class Volume(
    value: Number,
    unit: Unit,
) : Value<Number, Volume.Unit, Volume>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, Volume> {
        LITER("L"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.LITER -> when (unit) {
            Unit.LITER -> this
        }
    }

    companion object : Converter<Number, Unit, Volume>(::Volume) {
        /**
         * @see Unit.LITER
         */
        val Number.liters by valueProperty(Unit.LITER)
    }
}
