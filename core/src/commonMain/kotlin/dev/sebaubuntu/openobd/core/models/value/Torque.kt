/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Torque.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Torque">Wikipedia</a>
 */
class Torque(
    value: Number,
    unit: Unit,
) : Value<Number, Torque.Unit, Torque>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, Torque> {
        NEWTON_METER("N⋅m"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.NEWTON_METER -> when (unit) {
            Unit.NEWTON_METER -> this
        }
    }

    companion object : Converter<Number, Unit, Torque>(::Torque) {
        /**
         * @see Unit.NEWTON_METER
         */
        val Number.newtonMeters by valueProperty(Unit.NEWTON_METER)
    }
}
