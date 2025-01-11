/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.ext

import kotlinx.coroutines.flow.flow

/**
 * Like [kotlinx.coroutines.flow.flowOf], but lets you use suspend functions.
 * @see flow
 */
fun <T> flowOf(producer: suspend () -> T) = flow {
    emit(producer())
}
