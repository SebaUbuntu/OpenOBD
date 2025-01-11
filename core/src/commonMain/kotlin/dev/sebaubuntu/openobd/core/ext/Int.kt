/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.ext

inline operator fun <reified N : Number> Int.plus(other: N): Number = when (other) {
    is Byte -> plus(other)
    is Double -> plus(other)
    is Float -> plus(other)
    is Int -> plus(other)
    is Long -> plus(other)
    is Short -> plus(other)
    else -> error("Unsupported number class: ${other::class}")
}

inline operator fun <reified N : Number> Int.minus(other: N): Number = when (other) {
    is Byte -> minus(other)
    is Double -> minus(other)
    is Float -> minus(other)
    is Int -> minus(other)
    is Long -> minus(other)
    is Short -> minus(other)
    else -> error("Unsupported number class: ${other::class}")
}

inline operator fun <reified N : Number> Int.times(other: N): Number = when (other) {
    is Byte -> times(other)
    is Double -> times(other)
    is Float -> times(other)
    is Int -> times(other)
    is Long -> times(other)
    is Short -> times(other)
    else -> error("Unsupported number class: ${other::class}")
}

inline operator fun <reified N : Number> Int.div(other: N): Number = when (other) {
    is Byte -> div(other)
    is Double -> div(other)
    is Float -> div(other)
    is Int -> div(other)
    is Long -> div(other)
    is Short -> div(other)
    else -> error("Unsupported number class: ${other::class}")
}
