/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
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
    enum class Unit : Value.Unit<Number, Unit, Voltage> {
        MILLIVOLT,
        VOLT,
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
}
