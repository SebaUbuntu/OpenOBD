/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.logging

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Log entry.
 */
data class LogEntry(
    val message: String,
    val level: LogLevel,
    val tag: String? = null,
    val throwable: Throwable? = null,
    val timestamp: Instant = Clock.System.now(),
)
