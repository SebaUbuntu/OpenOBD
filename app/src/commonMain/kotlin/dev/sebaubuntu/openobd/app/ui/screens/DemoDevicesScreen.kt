/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import dev.sebaubuntu.openobd.app.viewmodels.DemoDevicesViewModel

@Composable
fun DemoDevicesScreen(
    paddingValues: PaddingValues,
) {
    BaseDevicesScreen<DemoDevicesViewModel, _, _>(
        paddingValues = paddingValues,
        permission = null,
    )
}
