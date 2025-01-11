/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Mass.
 */
class Mass(
    value: Number,
    unit: Unit,
) : Value<Number, Mass.Unit, Mass>(value, unit) {
    enum class Unit : Value.Unit<Number, Unit, Mass> {
        GRAM,
        KILOGRAM,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.GRAM -> when (unit) {
            Unit.GRAM -> this
            Unit.KILOGRAM -> Mass(value / 1000f, unit)
        }

        Unit.KILOGRAM -> when (unit) {
            Unit.GRAM -> Mass(value * 1000, unit)
            Unit.KILOGRAM -> this
        }
    }
}
