/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui

import dev.sebaubuntu.openobd.ui.screens.BluetoothDevicesScreen
import dev.sebaubuntu.openobd.ui.screens.CurrentDataScreen
import dev.sebaubuntu.openobd.ui.screens.DashboardScreen
import dev.sebaubuntu.openobd.ui.screens.DemoDevicesScreen
import dev.sebaubuntu.openobd.ui.screens.DevicesScreen
import dev.sebaubuntu.openobd.ui.screens.DiagnosticTroubleCodesScreen
import dev.sebaubuntu.openobd.ui.screens.FreezeFrameDataScreen
import dev.sebaubuntu.openobd.ui.screens.HomeScreen
import dev.sebaubuntu.openobd.ui.screens.LogsScreen
import dev.sebaubuntu.openobd.ui.screens.NetworkDevicesScreen
import dev.sebaubuntu.openobd.ui.screens.SessionInformationScreen
import dev.sebaubuntu.openobd.ui.screens.SettingsScreen
import dev.sebaubuntu.openobd.ui.screens.TerminalScreen
import dev.sebaubuntu.openobd.ui.screens.UsbDevicesScreen
import dev.sebaubuntu.openobd.ui.screens.VehicleInformationScreen
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.app_name
import openobd.app.generated.resources.current_data
import openobd.app.generated.resources.dashboard
import openobd.app.generated.resources.devices
import openobd.app.generated.resources.dtc
import openobd.app.generated.resources.freeze_frame_data
import openobd.app.generated.resources.logs
import openobd.app.generated.resources.session_information
import openobd.app.generated.resources.settings
import openobd.app.generated.resources.terminal
import openobd.app.generated.resources.vehicle_information
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
     * @see DemoDevicesScreen
     */
    DEMO_DEVICES(
        title = Res.string.devices,
    ),

    /**
     * @see NetworkDevicesScreen
     */
    NETWORK_DEVICES(
        title = Res.string.devices,
    ),

    /**
     * @see UsbDevicesScreen
     */
    USB_DEVICES(
        title = Res.string.devices,
    ),

    /**
     * @see SessionInformationScreen
     */
    SESSION_INFORMATION(
        title = Res.string.session_information,
    ),

    /**
     * @see DashboardScreen
     */
    DASHBOARD(
        title = Res.string.dashboard,
    ),

    /**
     * @see DiagnosticTroubleCodesScreen
     */
    DTC(
        title = Res.string.dtc,
    ),

    /**
     * @see CurrentDataScreen
     */
    CURRENT_DATA(
        title = Res.string.current_data,
    ),

    /**
     * @see FreezeFrameDataScreen
     */
    FREEZE_FRAME_DATA(
        title = Res.string.freeze_frame_data,
    ),

    /**
     * @see VehicleInformationScreen
     */
    VEHICLE_INFORMATION(
        title = Res.string.vehicle_information,
    ),

    /**
     * @see TerminalScreen
     */
    TERMINAL(
        title = Res.string.terminal,
    ),

    /**
     * @see LogsScreen
     */
    LOGS(
        title = Res.string.logs,
    ),

    /**
     * @see SettingsScreen
     */
    SETTINGS(
        title = Res.string.settings,
    ),
}
