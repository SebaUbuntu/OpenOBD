/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Voltage.
 */
class Voltage(
    value: Float,
    unit: Unit,
) : Value<Float, Voltage.Unit, Voltage>(value, unit) {
    enum class Unit : Value.Unit<Float, Unit, Voltage> {
        VOLT,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.VOLT -> when (unit) {
            Unit.VOLT -> this
        }
    }
}
