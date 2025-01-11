/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Current.
 */
class Current(
    value: Float,
    unit: Unit,
) : Value<Float, Current.Unit, Current>(value, unit) {
    enum class Unit : Value.Unit<Float, Unit, Current> {
        AMPERE,
        MILLIAMPERE,
    }

    override fun to(unit: Unit): Current = when (this.unit) {
        Unit.AMPERE -> when (unit) {
            Unit.AMPERE -> this
            Unit.MILLIAMPERE -> Current(value * 1000, unit)
        }

        Unit.MILLIAMPERE -> when (unit) {
            Unit.AMPERE -> Current(value / 1000, unit)
            Unit.MILLIAMPERE -> this
        }
    }
}
