/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models

/**
 * A value.
 *
 * @param value The value
 * @param unit The [Value.Unit]
 * @param T The type of the value
 * @param U The [Value.Unit] type
 * @param V [Value] itself
 */
sealed class Value<T, U : Value.Unit, V : Value<T, U, V>>(
    val value: T,
    val unit: U,
) {
    /**
     * The unit of the value.
     */
    interface Unit

    /**
     * Convert the value to another unit.
     *
     * @param unit The unit to convert to
     * @return The converted value
     */
    abstract fun to(unit: U): V

    override fun toString() = "$value $unit"

    /**
     * Acceleration.
     */
    class Acceleration(
        value: Float,
        unit: Unit,
    ) : Value<Float, Acceleration.Unit, Acceleration>(value, unit) {
        enum class Unit : Value.Unit {
            METER_PER_SQUARED_SECOND,
        }

        override fun to(unit: Unit) = to(unit) {
            TODO("Not yet implemented")
        }
    }

    /**
     * Current.
     */
    class Current(
        value: Float,
        unit: Unit,
    ) : Value<Float, Current.Unit, Current>(value, unit) {
        enum class Unit : Value.Unit {
            AMPERE,
        }

        override fun to(unit: Unit): Current = to(unit) {
            TODO("Not yet implemented")
        }
    }

    /**
     * Frequency.
     */
    class Frequency(
        value: Float,
        unit: Unit,
    ) : Value<Float, Frequency.Unit, Frequency>(value, unit) {
        enum class Unit : Value.Unit {
            HERTZ,
            REVOLUTIONS_PER_MINUTE,
        }

        override fun to(unit: Unit) = to(unit) {
            TODO("Not yet implemented")
        }
    }

    /**
     * Mass.
     */
    class Mass(
        value: Float,
        unit: Unit,
    ) : Value<Float, Mass.Unit, Mass>(value, unit) {
        enum class Unit : Value.Unit {
            KILOGRAM,
        }

        override fun to(unit: Unit) = to(unit) {
            TODO("Not yet implemented")
        }
    }

    /**
     * Length.
     */
    class Length(
        value: Float,
        unit: Unit,
    ) : Value<Float, Length.Unit, Length>(value, unit) {
        enum class Unit : Value.Unit {
            METER,
            KILOMETER,
        }

        override fun to(unit: Unit) = to(unit) {
            TODO("Not yet implemented")
        }
    }

    /**
     * Percentage.
     */
    class Percentage(
        value: Float,
        unit: Unit,
    ) : Value<Float, Percentage.Unit, Percentage>(value, unit) {
        enum class Unit : Value.Unit {
            PERCENT,
        }

        override fun to(unit: Unit) = to(unit) {
            TODO("Not yet implemented")
        }
    }

    /**
     * Power.
     */
    class Power(
        value: Float,
        unit: Unit,
    ) : Value<Float, Power.Unit, Power>(value, unit) {
        enum class Unit : Value.Unit {
            WATT,
        }

        override fun to(unit: Unit) = to(unit) {
            TODO("Not yet implemented")
        }
    }

    /**
     * Speed.
     */
    class Speed(
        value: Float,
        unit: Unit,
    ) : Value<Float, Speed.Unit, Speed>(value, unit) {
        enum class Unit : Value.Unit {
            METER_PER_SECOND,
            KILOMETER_PER_HOUR,
        }

        override fun to(unit: Unit) = to(unit) {
            TODO("Not yet implemented")
        }
    }

    /**
     * Temperature.
     */
    class Temperature(
        value: Float,
        unit: Unit,
    ) : Value<Float, Temperature.Unit, Temperature>(value, unit) {
        enum class Unit : Value.Unit {
            CELSIUS,
            FAHRENHEIT,
            KELVIN,
        }

        override fun to(unit: Unit) = to(unit) {
            TODO("Not yet implemented")
        }
    }

    /**
     * Time.
     */
    class Time(
        value: Long,
        unit: Unit,
    ) : Value<Long, Time.Unit, Time>(value, unit) {
        enum class Unit : Value.Unit {
            SECOND,
        }

        override fun to(unit: Unit) = to(unit) {
            TODO("Not yet implemented")
        }
    }

    /**
     * Voltage.
     */
    class Voltage(
        value: Float,
        unit: Unit,
    ) : Value<Float, Voltage.Unit, Voltage>(value, unit) {
        enum class Unit : Value.Unit {
            VOLT,
        }

        override fun to(unit: Unit) = to(unit) {
            TODO("Not yet implemented")
        }
    }

    companion object {
        private inline fun <T, U : Unit, V : Value<T, U, V>> V.to(
            unit: U,
            noinline converter: (V) -> V
        ): V = when (this.unit) {
            unit -> this
            else -> converter(this)
        }
    }
}
