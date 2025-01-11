/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Power.
 */
class Power(
    value: Number,
    unit: Unit,
) : Value<Number, Power.Unit, Power>(value, unit) {
    enum class Unit : Value.Unit<Number, Unit, Power> {
        WATT,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.WATT -> when (unit) {
            Unit.WATT -> this
        }
    }
}
