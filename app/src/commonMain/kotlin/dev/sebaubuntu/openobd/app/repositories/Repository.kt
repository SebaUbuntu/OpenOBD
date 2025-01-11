/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope

abstract class Repository(
    protected val coroutineScope: CoroutineScope,
    protected val coroutineDispatcher: CoroutineDispatcher,
)
