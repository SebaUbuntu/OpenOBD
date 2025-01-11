/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.ext

fun UInt.getBit(byte: Int): Boolean = and(0x1u.shl(byte)) != 0u
