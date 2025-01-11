/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui

import androidx.compose.runtime.staticCompositionLocalOf
import dev.sebaubuntu.openobd.utils.PermissionsManager

val LocalPermissionsManager = staticCompositionLocalOf { PermissionsManager.DEFAULT }
