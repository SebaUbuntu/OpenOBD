/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ext

import dev.sebaubuntu.openobd.app.ui.navigation.AppNavRoute
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

val AppNavRoute.stringResource: StringResource
    get() = when (this) {
        AppNavRoute.Home -> Res.string.app_name
        AppNavRoute.DeviceTypes -> Res.string.devices
        is AppNavRoute.Devices -> deviceType.stringResource
        AppNavRoute.SessionInformation -> Res.string.session_information
        AppNavRoute.Dashboard -> Res.string.dashboard
        AppNavRoute.DiagnosticTroubleCodes -> Res.string.dtc
        AppNavRoute.CurrentData -> Res.string.current_data
        AppNavRoute.FreezeFrameData -> Res.string.freeze_frame_data
        AppNavRoute.VehicleInformation -> Res.string.vehicle_information
        AppNavRoute.Terminal -> Res.string.terminal
        AppNavRoute.Logs -> Res.string.logs
        AppNavRoute.Settings -> Res.string.settings
    }
