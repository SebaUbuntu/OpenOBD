/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.ext

inline fun <reified N : Number> Number.toNumber(): N = when (N::class) {
    Byte::class -> toByte()
    Double::class -> toDouble()
    Float::class -> toFloat()
    Int::class -> toInt()
    Long::class -> toLong()
    Number::class -> this
    Short::class -> toShort()
    else -> error("Unsupported number class: ${N::class}")
} as N

inline operator fun <reified N : Number> N.plus(other: Number): Number = when (this) {
    is Byte -> plus(other)
    is Double -> plus(other)
    is Float -> plus(other)
    is Int -> plus(other)
    is Long -> plus(other)
    is Short -> plus(other)
    else -> error("Unsupported number class: ${this::class}")
}

inline operator fun <reified N : Number> N.minus(other: Number): Number = when (this) {
    is Byte -> minus(other)
    is Double -> minus(other)
    is Float -> minus(other)
    is Int -> minus(other)
    is Long -> minus(other)
    is Short -> minus(other)
    else -> error("Unsupported number class: ${this::class}")
}

inline operator fun <reified N : Number> N.times(other: Number): Number = when (this) {
    is Byte -> times(other)
    is Double -> times(other)
    is Float -> times(other)
    is Int -> times(other)
    is Long -> times(other)
    is Short -> times(other)
    else -> error("Unsupported number class: ${this::class}")
}

inline operator fun <reified N : Number> N.div(other: Number): Number = when (this) {
    is Byte -> div(other)
    is Double -> div(other)
    is Float -> div(other)
    is Int -> div(other)
    is Long -> div(other)
    is Short -> div(other)
    else -> error("Unsupported number class: ${this::class}")
}
