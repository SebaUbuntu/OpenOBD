/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models.value

import dev.sebaubuntu.openobd.core.ext.toNumber

/**
 * A value.
 *
 * @param value The value
 * @param unit The [Value.Unit]
 * @param T The type of the value
 * @param U The [Value.Unit] type
 * @param V [Value] itself
 */
sealed class Value<T : Number, U : Value.Unit<T, U, V>, V : Value<T, U, V>>(
    val value: T,
    val unit: U,
) {
    /**
     * The unit of the value.
     */
    sealed interface Unit<T : Number, U : Unit<T, U, V>, V : Value<T, U, V>>

    /**
     * Convert the value to another unit.
     *
     * @param unit The unit to convert to
     * @return The converted value
     */
    abstract fun to(unit: U): V

    override fun toString() = "$value $unit"

    companion object {
        /**
         * Convert a number to a typed [Value].
         */
        inline fun <T : Number, U : Unit<T, U, V>, reified V : Value<T, U, V>> T.asValue(
            unit: Unit<T, U, V>
        ) = when (unit) {
            is Acceleration.Unit -> Acceleration(this.toNumber(), unit)
            is Angle.Unit -> Angle(this.toNumber(), unit)
            is Current.Unit -> Current(this.toNumber(), unit)
            is Frequency.Unit -> Frequency(this.toNumber(), unit)
            is Mass.Unit -> Mass(this.toNumber(), unit)
            is MassFlowRate.Unit -> MassFlowRate(this.toNumber(), unit)
            is Length.Unit -> Length(this.toNumber(), unit)
            is Percentage.Unit -> Percentage(this.toNumber(), unit)
            is Power.Unit -> Power(this.toNumber(), unit)
            is Pressure.Unit -> Pressure(this.toNumber(), unit)
            is Speed.Unit -> Speed(this.toNumber(), unit)
            is Temperature.Unit -> Temperature(this.toNumber(), unit)
            is Time.Unit -> Time(this.toNumber(), unit)
            is Torque.Unit -> Torque(this.toNumber(), unit)
            is Voltage.Unit -> Voltage(this.toNumber(), unit)
            is Volume.Unit -> Volume(this.toNumber(), unit)
            is VolumeFlowRate.Unit -> VolumeFlowRate(this.toNumber(), unit)
        } as V
    }
}
