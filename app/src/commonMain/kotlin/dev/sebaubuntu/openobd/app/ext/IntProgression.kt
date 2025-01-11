/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ext

/**
 * Map a value included in this progression to a percentage value between [0f, 1f].
 */
fun IntProgression.valueToPercentage(value: Int) = (value - first).toFloat() / (last - first)
