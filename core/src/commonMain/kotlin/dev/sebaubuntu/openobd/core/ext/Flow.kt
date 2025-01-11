/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.ext

import kotlinx.coroutines.flow.asFlow

/**
 * @see asFlow
 */
fun <T> flowOf(producer: suspend () -> T) = producer.asFlow()
