/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.ext

/**
 * Return this object as a [C] object.
 *
 * @param C The type of the return
 */
inline operator fun <reified C> C.invoke(): C = this
