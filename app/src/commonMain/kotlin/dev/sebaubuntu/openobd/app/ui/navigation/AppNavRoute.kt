/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.navigation

import androidx.navigation3.runtime.NavKey
import dev.sebaubuntu.openobd.app.ui.screens.CurrentDataScreen
import dev.sebaubuntu.openobd.app.ui.screens.DashboardScreen
import dev.sebaubuntu.openobd.app.ui.screens.DeviceTypesScreen
import dev.sebaubuntu.openobd.app.ui.screens.DevicesScreen
import dev.sebaubuntu.openobd.app.ui.screens.DiagnosticTroubleCodesScreen
import dev.sebaubuntu.openobd.app.ui.screens.FreezeFrameDataScreen
import dev.sebaubuntu.openobd.app.ui.screens.HomeScreen
import dev.sebaubuntu.openobd.app.ui.screens.LogsScreen
import dev.sebaubuntu.openobd.app.ui.screens.SessionInformationScreen
import dev.sebaubuntu.openobd.app.ui.screens.SettingsScreen
import dev.sebaubuntu.openobd.app.ui.screens.TerminalScreen
import dev.sebaubuntu.openobd.app.ui.screens.VehicleInformationScreen
import dev.sebaubuntu.openobd.network.devices.models.DeviceType
import kotlinx.serialization.Serializable

/**
 * App routes. Each route has an associated screen.
 */
@Serializable
sealed interface AppNavRoute : NavKey {
    /**
     * @see HomeScreen
     */
    @Serializable
    data object Home : AppNavRoute

    /**
     * @see DeviceTypesScreen
     */
    @Serializable
    data object DeviceTypes : AppNavRoute

    /**
     * @see DevicesScreen
     */
    @Serializable
    data class Devices(val deviceType: DeviceType) : AppNavRoute

    /**
     * @see SessionInformationScreen
     */
    @Serializable
    data object SessionInformation : AppNavRoute

    /**
     * @see DashboardScreen
     */
    @Serializable
    data object Dashboard : AppNavRoute

    /**
     * @see DiagnosticTroubleCodesScreen
     */
    @Serializable
    data object DiagnosticTroubleCodes : AppNavRoute

    /**
     * @see CurrentDataScreen
     */
    @Serializable
    data object CurrentData : AppNavRoute

    /**
     * @see FreezeFrameDataScreen
     */
    @Serializable
    data object FreezeFrameData : AppNavRoute

    /**
     * @see VehicleInformationScreen
     */
    @Serializable
    data object VehicleInformation : AppNavRoute

    /**
     * @see TerminalScreen
     */
    @Serializable
    data object Terminal : AppNavRoute

    /**
     * @see LogsScreen
     */
    @Serializable
    data object Logs : AppNavRoute

    /**
     * @see SettingsScreen
     */
    @Serializable
    data object Settings : AppNavRoute
}
