/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import dev.sebaubuntu.openobd.models.Permission
import dev.sebaubuntu.openobd.viewmodels.BluetoothDevicesViewModel

/**
 * Bluetooth device selector screen.
 */
@Composable
fun BluetoothDevicesScreen(
    paddingValues: PaddingValues,
) {
    BaseDevicesScreen<BluetoothDevicesViewModel, _, _>(
        paddingValues = paddingValues,
        permission = Permission.BLUETOOTH,
    )
}
