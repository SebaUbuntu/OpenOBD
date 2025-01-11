/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Volume flow rate.
 *
 * @see Volume
 */
class VolumeFlowRate(
    value: Number,
    unit: Unit,
) : Value<Number, VolumeFlowRate.Unit, VolumeFlowRate>(value, unit) {
    enum class Unit(override val symbol: String?) : Value.Unit<Number, Unit, VolumeFlowRate> {
        LITER_PER_HOUR("L/h"),
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.LITER_PER_HOUR -> when (unit) {
            Unit.LITER_PER_HOUR -> this
        }
    }

    companion object : Converter<Number, Unit, VolumeFlowRate>(::VolumeFlowRate) {
        /**
         * @see Unit.LITER_PER_HOUR
         */
        val Number.litersPerHour by valueProperty(Unit.LITER_PER_HOUR)
    }
}
