/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.ext

fun UInt.getBit(byte: Int): Boolean = and(0x1u.shl(byte)) != 0u
