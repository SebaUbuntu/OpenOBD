/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

abstract class Repository(
    protected val coroutineScope: CoroutineScope,
    protected val coroutineDispatcher: CoroutineDispatcher,
)
