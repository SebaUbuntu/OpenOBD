/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.logging

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Log entry.
 */
@OptIn(ExperimentalTime::class)
data class LogEntry(
    val message: String,
    val level: LogLevel,
    val tag: String? = null,
    val throwable: Throwable? = null,
    val timestamp: Instant = Clock.System.now(),
)
