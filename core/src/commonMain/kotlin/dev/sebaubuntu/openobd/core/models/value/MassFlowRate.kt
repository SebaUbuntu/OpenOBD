/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Mass flow rate.
 *
 * @see Mass
 */
class MassFlowRate(
    value: Number,
    unit: Unit,
) : Value<Number, MassFlowRate.Unit, MassFlowRate>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, MassFlowRate> {
        GRAM_PER_SECOND("g/s"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.GRAM_PER_SECOND -> when (unit) {
            Unit.GRAM_PER_SECOND -> this
        }
    }

    companion object : Converter<Number, Unit, MassFlowRate>(::MassFlowRate) {
        /**
         * @see Unit.GRAM_PER_SECOND
         */
        val Number.gramsPerSecond by valueProperty(Unit.GRAM_PER_SECOND)
    }
}
