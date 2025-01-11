/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Volume.
 */
class Volume(
    value: Number,
    unit: Unit,
) : Value<Number, Volume.Unit, Volume>(value, unit) {
    enum class Unit : Value.Unit<Number, Unit, Volume> {
        LITER,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.LITER -> when (unit) {
            Unit.LITER -> this
        }
    }
}
