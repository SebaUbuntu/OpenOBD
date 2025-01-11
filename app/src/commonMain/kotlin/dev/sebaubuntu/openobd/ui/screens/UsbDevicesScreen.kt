/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import dev.sebaubuntu.openobd.viewmodels.UsbDevicesViewModel

@Composable
fun UsbDevicesScreen(
    paddingValues: PaddingValues,
) {
    BaseDevicesScreen<UsbDevicesViewModel, _, _>(
        paddingValues = paddingValues,
        permission = null,
    )
}
