/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Volume.
 */
class Volume(
    value: Float,
    unit: Unit,
) : Value<Float, Volume.Unit, Volume>(value, unit) {
    enum class Unit : Value.Unit<Float, Unit, Volume> {
        LITER,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.LITER -> when (unit) {
            Unit.LITER -> this
        }
    }
}
