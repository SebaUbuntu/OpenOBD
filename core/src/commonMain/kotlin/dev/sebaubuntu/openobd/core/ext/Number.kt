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

inline operator fun <reified N : Number> N.compareTo(other: Number): Int = when (this) {
    is Byte -> compareTo(other)
    is Double -> compareTo(other)
    is Float -> compareTo(other)
    is Int -> compareTo(other)
    is Long -> compareTo(other)
    is Short -> compareTo(other)
    else -> error("Unsupported number class: ${this::class}")
}

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

inline operator fun <reified N : Number> N.rem(other: Number): Number = when (this) {
    is Byte -> rem(other)
    is Double -> rem(other)
    is Float -> rem(other)
    is Int -> rem(other)
    is Long -> rem(other)
    is Short -> rem(other)
    else -> error("Unsupported number class: ${this::class}")
}

inline operator fun <reified N : Number> N.inc(): N = when (this) {
    is Byte -> inc()
    is Double -> inc()
    is Float -> inc()
    is Int -> inc()
    is Long -> inc()
    is Short -> inc()
    else -> error("Unsupported number class: ${this::class}")
} as N

inline operator fun <reified N : Number> N.dec(): N = when (this) {
    is Byte -> dec()
    is Double -> dec()
    is Float -> dec()
    is Int -> dec()
    is Long -> dec()
    is Short -> dec()
    else -> error("Unsupported number class: ${this::class}")
} as N

inline operator fun <reified N : Number> N.unaryPlus(): N = when (this) {
    is Byte -> unaryPlus()
    is Double -> unaryPlus()
    is Float -> unaryPlus()
    is Int -> unaryPlus()
    is Long -> unaryPlus()
    is Short -> unaryPlus()
    else -> error("Unsupported number class: ${this::class}")
} as N

inline operator fun <reified N : Number> N.unaryMinus(): N = when (this) {
    is Byte -> unaryMinus()
    is Double -> unaryMinus()
    is Float -> unaryMinus()
    is Int -> unaryMinus()
    is Long -> unaryMinus()
    is Short -> unaryMinus()
    else -> error("Unsupported number class: ${this::class}")
} as N
