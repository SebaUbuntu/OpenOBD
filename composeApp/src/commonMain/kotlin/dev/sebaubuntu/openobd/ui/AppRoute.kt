/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui

import dev.sebaubuntu.openobd.ui.screens.BluetoothDevicesScreen
import dev.sebaubuntu.openobd.ui.screens.DashboardScreen
import dev.sebaubuntu.openobd.ui.screens.DevicesScreen
import dev.sebaubuntu.openobd.ui.screens.DtcScreen
import dev.sebaubuntu.openobd.ui.screens.HomeScreen
import dev.sebaubuntu.openobd.ui.screens.SensorsScreen
import dev.sebaubuntu.openobd.ui.screens.SettingsScreen
import openobd.composeapp.generated.resources.Res
import openobd.composeapp.generated.resources.app_name
import openobd.composeapp.generated.resources.dashboard
import openobd.composeapp.generated.resources.devices
import openobd.composeapp.generated.resources.dtc
import openobd.composeapp.generated.resources.sensors
import openobd.composeapp.generated.resources.settings
import org.jetbrains.compose.resources.StringResource

/**
 * App routes. Each route has an associated screen.
 */
enum class AppRoute(
    val title: StringResource,
) {
    /**
     * @see HomeScreen
     */
    HOME(
        title = Res.string.app_name,
    ),

    /**
     * @see DevicesScreen
     */
    DEVICES(
        title = Res.string.devices,
    ),

    /**
     * @see BluetoothDevicesScreen
     */
    BLUETOOTH_DEVICES(
        title = Res.string.devices,
    ),

    /**
     * @see DashboardScreen
     */
    DASHBOARD(
        title = Res.string.dashboard,
    ),

    /**
     * @see DtcScreen
     */
    DTC(
        title = Res.string.dtc,
    ),

    /**
     * @see SensorsScreen
     */
    SENSORS(
        title = Res.string.sensors,
    ),

    /**
     * @see SettingsScreen
     */
    SETTINGS(
        title = Res.string.settings,
    ),
}
