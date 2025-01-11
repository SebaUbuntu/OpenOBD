/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ext

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.eventFlow
import kotlinx.coroutines.flow.mapNotNull

/**
 * Emit a [Unit] only when the lifecycle reaches the requested event
 * @see Lifecycle.eventFlow
 */
fun Lifecycle.eventFlow(event: Lifecycle.Event) = eventFlow
    .mapNotNull {
        when (it) {
            event -> Unit
            else -> null
        }
    }
