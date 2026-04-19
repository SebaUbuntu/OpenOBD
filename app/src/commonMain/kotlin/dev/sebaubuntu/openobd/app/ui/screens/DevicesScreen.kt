/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import dev.sebaubuntu.openobd.app.models.Permission
import dev.sebaubuntu.openobd.app.viewmodels.BluetoothDevicesViewModel
import dev.sebaubuntu.openobd.app.viewmodels.BluetoothLeDevicesViewModel
import dev.sebaubuntu.openobd.app.viewmodels.DemoDevicesViewModel
import dev.sebaubuntu.openobd.app.viewmodels.UsbDevicesViewModel
import dev.sebaubuntu.openobd.backend.models.DeviceType

@Composable
fun DevicesScreen(
    paddingValues: PaddingValues,
    deviceType: DeviceType,
) {
    when (deviceType) {
        DeviceType.BLUETOOTH -> BaseDevicesScreen<BluetoothDevicesViewModel, _, _>(
            paddingValues = paddingValues,
            permission = Permission.BLUETOOTH,
        )

        DeviceType.BLUETOOTH_LE -> BaseDevicesScreen<BluetoothLeDevicesViewModel, _, _>(
            paddingValues = paddingValues,
            permission = Permission.BLUETOOTH,
        )

        DeviceType.DEMO -> BaseDevicesScreen<DemoDevicesViewModel, _, _>(
            paddingValues = paddingValues,
            permission = null,
        )

        DeviceType.NETWORK -> NetworkDevicesScreen(paddingValues)

        DeviceType.SERIAL -> TODO()

        DeviceType.USB -> BaseDevicesScreen<UsbDevicesViewModel, _, _>(
            paddingValues = paddingValues,
            permission = null,
        )
    }
}
