/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Percentage.
 */
class Percentage(
    value: Float,
    unit: Unit,
) : Value<Float, Percentage.Unit, Percentage>(value, unit) {
    enum class Unit : Value.Unit<Float, Unit, Percentage> {
        PERCENT,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.PERCENT -> when (unit) {
            Unit.PERCENT -> this
        }
    }
}
