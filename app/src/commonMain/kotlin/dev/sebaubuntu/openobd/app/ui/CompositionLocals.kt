/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavBackStack
import dev.sebaubuntu.openobd.app.ui.navigation.AppNavRoute

val LocalNavBackStack = compositionLocalOf<NavBackStack<AppNavRoute>> {
    error("LocalNavBackStack not initialized")
}
