/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Mass.
 */
class Mass(
    value: Float,
    unit: Unit,
) : Value<Float, Mass.Unit, Mass>(value, unit) {
    enum class Unit : Value.Unit<Float, Unit, Mass> {
        GRAM,
        KILOGRAM,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.GRAM -> when (unit) {
            Unit.GRAM -> this
            Unit.KILOGRAM -> Mass(value / 1000, unit)
        }

        Unit.KILOGRAM -> when (unit) {
            Unit.GRAM -> Mass(value * 1000, unit)
            Unit.KILOGRAM -> this
        }
    }
}
