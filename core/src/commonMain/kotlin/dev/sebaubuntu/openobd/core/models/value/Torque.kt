/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Torque.
 *
 * @see <a href="https://en.wikipedia.org/wiki/Torque">Wikipedia</a>
 */
class Torque(
    value: Number,
    unit: Unit,
) : Value<Number, Torque.Unit, Torque>(value, unit) {
    enum class Unit : Value.Unit<Number, Unit, Torque> {
        NEWTON_METER,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.NEWTON_METER -> when (unit) {
            Unit.NEWTON_METER -> this
        }
    }
}
