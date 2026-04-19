/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.sebaubuntu.openobd.app.ui.LocalNavBackStack
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

@Composable
fun OpenOBDNavDisplay(
    paddingValues: PaddingValues,
    navBackStack: NavBackStack<AppNavRoute>,
    modifier: Modifier = Modifier,
) {
    val entryProvider = entryProvider {
        entry<AppNavRoute.Home> {
            HomeScreen(
                paddingValues = paddingValues,
            )
        }

        entry<AppNavRoute.DeviceTypes> {
            DeviceTypesScreen(
                paddingValues = paddingValues,
            )
        }

        entry<AppNavRoute.Devices> {
            DevicesScreen(
                paddingValues = paddingValues,
                deviceType = it.deviceType,
            )
        }

        entry<AppNavRoute.SessionInformation> {
            SessionInformationScreen(
                paddingValues = paddingValues,
            )
        }

        entry<AppNavRoute.Dashboard> {
            DashboardScreen(
                paddingValues = paddingValues,
            )
        }

        entry<AppNavRoute.DiagnosticTroubleCodes> {
            DiagnosticTroubleCodesScreen(
                paddingValues = paddingValues,
            )
        }

        entry<AppNavRoute.CurrentData> {
            CurrentDataScreen(
                paddingValues = paddingValues,
            )
        }

        entry<AppNavRoute.FreezeFrameData> {
            FreezeFrameDataScreen(
                paddingValues = paddingValues,
            )
        }

        entry<AppNavRoute.VehicleInformation> {
            VehicleInformationScreen(
                paddingValues = paddingValues,
            )
        }

        entry<AppNavRoute.Terminal> {
            TerminalScreen(
                paddingValues = paddingValues,
            )
        }

        entry<AppNavRoute.Logs> {
            LogsScreen(
                paddingValues = paddingValues,
            )
        }

        entry<AppNavRoute.Settings> {
            SettingsScreen(
                paddingValues = paddingValues,
            )
        }
    }

    CompositionLocalProvider(
        LocalNavBackStack provides navBackStack,
    ) {
        NavDisplay(
            backStack = navBackStack,
            modifier = modifier,
            entryDecorators = listOf(
                // Add the default decorators for managing scenes and saving state
                rememberSaveableStateHolderNavEntryDecorator(),
                // Then add the view model store decorator
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider,
        )
    }
}
