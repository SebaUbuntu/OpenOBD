/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Percentage.
 */
class Percentage(
    value: Number,
    unit: Unit,
) : Value<Number, Percentage.Unit, Percentage>(value, unit) {
    enum class Unit : Value.Unit<Number, Unit, Percentage> {
        PERCENT,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.PERCENT -> when (unit) {
            Unit.PERCENT -> this
        }
    }
}
