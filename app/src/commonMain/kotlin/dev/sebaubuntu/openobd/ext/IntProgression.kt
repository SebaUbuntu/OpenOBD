/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ext

/**
 * Map a value included in this progression to a percentage value between [0f, 1f].
 */
fun IntProgression.valueToPercentage(value: Int) = (value - first).toFloat() / (last - first)
