/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ext

import kotlin.math.PI

fun Double.degreesToRadians(): Double = this / 180.0 * PI

fun Double.radiansToDegrees(): Double = this * 180.0 / PI
