/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ext

fun IntProgression.valueToPercentage(value: Int) = (value - first).toFloat() / (last - first)
