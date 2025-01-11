/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Volume flow rate.
 *
 * @see Volume
 * @see Time
 */
class VolumeFlowRate(
    value: Number,
    unit: Unit,
) : Value<Number, VolumeFlowRate.Unit, VolumeFlowRate>(value, unit) {
    enum class Unit : Value.Unit<Number, Unit, VolumeFlowRate> {
        LITER_PER_HOUR,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.LITER_PER_HOUR -> when (unit) {
            Unit.LITER_PER_HOUR -> this
        }
    }
}
