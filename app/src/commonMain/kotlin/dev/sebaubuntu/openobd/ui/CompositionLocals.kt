/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui

import androidx.compose.runtime.staticCompositionLocalOf
import dev.sebaubuntu.openobd.models.PlatformContext
import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothManager
import dev.sebaubuntu.openobd.utils.PermissionsManager

val LocalPlatformContext = staticCompositionLocalOf<PlatformContext> {
    error("No platform context provided")
}

val LocalPermissionsManager = staticCompositionLocalOf { PermissionsManager.DEFAULT }

val LocalBluetoothManager = staticCompositionLocalOf { BluetoothManager.DEFAULT }
