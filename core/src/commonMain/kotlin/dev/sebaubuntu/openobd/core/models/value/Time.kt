/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.div
import dev.sebaubuntu.openobd.core.ext.times

/**
 * Time.
 */
class Time(
    value: Number,
    unit: Unit,
) : Value<Number, Time.Unit, Time>(value, unit) {
    enum class Unit : Value.Unit<Number, Unit, Time> {
        SECOND,
        MINUTE,
    }

    override fun to(unit: Unit) = when (this.unit) {
        Unit.SECOND -> when (unit) {
            Unit.SECOND -> this
            Unit.MINUTE -> Time(value / 60f, Unit.MINUTE)
        }

        Unit.MINUTE -> when (unit) {
            Unit.SECOND -> Time(value * 60, Unit.SECOND)
            Unit.MINUTE -> this
        }
    }
}
