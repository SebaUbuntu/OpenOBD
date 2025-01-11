/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.ext

import kotlinx.coroutines.flow.asFlow

/**
 * @see asFlow
 */
fun <T> flowOf(producer: suspend () -> T) = producer.asFlow()
