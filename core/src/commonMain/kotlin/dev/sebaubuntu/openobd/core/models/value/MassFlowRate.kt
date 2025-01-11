/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Mass flow rate.
 *
 * @see Mass
 * @see Time
 */
class MassFlowRate(
    value: Number,
    unit: Unit,
) : Value<Number, MassFlowRate.Unit, MassFlowRate>(value, unit) {
    enum class Unit : Value.Unit<Number, Unit, MassFlowRate> {
        GRAM_PER_SECOND,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.GRAM_PER_SECOND -> when (unit) {
            Unit.GRAM_PER_SECOND -> this
        }
    }
}
