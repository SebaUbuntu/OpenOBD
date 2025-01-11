/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.models.value

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

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
    sealed interface Unit<T : Number, U : Unit<T, U, V>, V : Value<T, U, V>> {
        /**
         * The symbol of the unit.
         */
        val symbol: String?
    }

    /**
     * An interface implemented by value's companion objects to ease up conversion.
     *
     * @param constructor The class constructor
     */
    abstract class Converter<T : Number, U : Unit<T, U, V>, V : Value<T, U, V>>(
        private val constructor: (T, U) -> V,
    ) {
        protected fun valueProperty(unit: U) = ReadOnlyProperty { thisRef: T, _: KProperty<*> ->
            constructor(thisRef, unit)
        }
    }

    /**
     * Convert the value to another unit.
     *
     * @param unit The unit to convert to
     * @return The converted value
     */
    abstract fun to(unit: U): V

    override fun toString() = unit.symbol?.let { symbol ->
        "$value $symbol"
    } ?: "$value"
}
