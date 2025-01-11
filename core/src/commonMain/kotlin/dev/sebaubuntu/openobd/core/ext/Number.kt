/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.ext

inline fun <reified N : Number> Number.toNumber(): N = when (N::class) {
    Byte::class -> toByte()
    Double::class -> toDouble()
    Float::class -> toFloat()
    Int::class -> toInt()
    Long::class -> toLong()
    Short::class -> toShort()
    else -> error("Unsupported number class: ${N::class}")
} as N
