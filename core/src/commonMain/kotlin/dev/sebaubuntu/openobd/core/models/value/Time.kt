/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

/**
 * Time.
 */
class Time(
    value: Long,
    unit: Unit,
) : Value<Long, Time.Unit, Time>(value, unit) {
    enum class Unit : Value.Unit<Long, Unit, Time> {
        SECOND,
        MINUTE,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.SECOND -> when (unit) {
            Unit.SECOND -> this
            Unit.MINUTE -> Time(value / 60, Unit.MINUTE)
        }

        Unit.MINUTE -> when (unit) {
            Unit.SECOND -> Time(value * 60, Unit.SECOND)
            Unit.MINUTE -> this
        }
    }
}
