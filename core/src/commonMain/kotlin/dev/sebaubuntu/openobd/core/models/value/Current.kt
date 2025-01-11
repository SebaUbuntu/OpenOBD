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
    }

    override fun to(unit: Unit): Current = when (this.unit) {
        Unit.AMPERE -> when (unit) {
            Unit.AMPERE -> this
        }
    }
}
