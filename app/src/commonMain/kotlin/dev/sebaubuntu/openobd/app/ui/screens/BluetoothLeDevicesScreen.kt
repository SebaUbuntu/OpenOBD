/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import dev.sebaubuntu.openobd.app.models.Permission
import dev.sebaubuntu.openobd.app.viewmodels.BluetoothLeDevicesViewModel

/**
 * Bluetooth LE device selector screen.
 */
@Composable
fun BluetoothLeDevicesScreen(
    paddingValues: PaddingValues,
) {
    BaseDevicesScreen<BluetoothLeDevicesViewModel, _, _>(
        paddingValues = paddingValues,
        permission = Permission.BLUETOOTH,
    )
}
