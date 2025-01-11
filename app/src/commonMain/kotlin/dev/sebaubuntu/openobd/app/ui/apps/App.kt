/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.apps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.sebaubuntu.openobd.app.repositories.ConnectionStatusRepository
import dev.sebaubuntu.openobd.app.ui.AppRoute
import dev.sebaubuntu.openobd.app.ui.composables.OpenOBDBottomBar
import dev.sebaubuntu.openobd.app.ui.composables.OpenOBDTopAppBar
import dev.sebaubuntu.openobd.app.ui.screens.BluetoothDevicesScreen
import dev.sebaubuntu.openobd.app.ui.screens.BluetoothLeDevicesScreen
import dev.sebaubuntu.openobd.app.ui.screens.CurrentDataScreen
import dev.sebaubuntu.openobd.app.ui.screens.DashboardScreen
import dev.sebaubuntu.openobd.app.ui.screens.DemoDevicesScreen
import dev.sebaubuntu.openobd.app.ui.screens.DevicesScreen
import dev.sebaubuntu.openobd.app.ui.screens.DiagnosticTroubleCodesScreen
import dev.sebaubuntu.openobd.app.ui.screens.FreezeFrameDataScreen
import dev.sebaubuntu.openobd.app.ui.screens.HomeScreen
import dev.sebaubuntu.openobd.app.ui.screens.LogsScreen
import dev.sebaubuntu.openobd.app.ui.screens.NetworkDevicesScreen
import dev.sebaubuntu.openobd.app.ui.screens.SessionInformationScreen
import dev.sebaubuntu.openobd.app.ui.screens.SettingsScreen
import dev.sebaubuntu.openobd.app.ui.screens.TerminalScreen
import dev.sebaubuntu.openobd.app.ui.screens.UsbDevicesScreen
import dev.sebaubuntu.openobd.app.ui.screens.VehicleInformationScreen
import dev.sebaubuntu.openobd.app.ui.themes.OpenOBDTheme
import dev.sebaubuntu.openobd.app.viewmodels.AppViewModel
import dev.sebaubuntu.openobd.app.viewmodels.CurrentDeviceViewModel
import dev.sebaubuntu.openobd.backend.models.DeviceType
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.getOrNull
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * App entry point.
 */
@Composable
fun App() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = AppRoute.valueOf(
        backStackEntry?.destination?.route ?: AppRoute.HOME.name
    )

    val appViewModel = koinViewModel<AppViewModel>()

    val theme by appViewModel.theme.collectAsStateWithLifecycle()
    val dynamicColors by appViewModel.dynamicColors.collectAsStateWithLifecycle()

    val currentDeviceViewModel = koinViewModel<CurrentDeviceViewModel>()
    val connectionStatusRepository = koinInject<ConnectionStatusRepository>()

    val device by currentDeviceViewModel.device.collectAsStateWithLifecycle()
    val connectionStatus by connectionStatusRepository.connectionStatus.collectAsStateWithLifecycle()

    OpenOBDTheme(
        theme = theme,
        dynamicColors = dynamicColors,
    ) {
        Scaffold(
            topBar = {
                OpenOBDTopAppBar(
                    currentRoute = currentRoute,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    onNavigateUp = { navController.navigateUp() },
                    onNavigateToSessionInformation = {
                        navController.navigate(AppRoute.SESSION_INFORMATION.name) {
                            popUpTo(AppRoute.SESSION_INFORMATION.name) {
                                inclusive = true
                            }
                        }
                    },
                    connectionStatus = connectionStatus,
                )
            },
            bottomBar = {
                OpenOBDBottomBar(
                    onNavigateToHome = {
                        navController.navigate(AppRoute.HOME.name) {
                            popUpTo(AppRoute.HOME.name) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToDevices = {
                        navController.navigate(AppRoute.DEVICES.name) {
                            popUpTo(AppRoute.DEVICES.name) {
                                inclusive = true
                            }
                        }
                    },
                    device = device.getOrNull(),
                    connectionStatus = connectionStatus,
                )
            },
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = AppRoute.HOME.name,
                modifier = Modifier.fillMaxSize(),
            ) {
                composable(route = AppRoute.HOME.name) {
                    HomeScreen(
                        paddingValues = paddingValues,
                        onNavigateTo = { route ->
                            navController.navigate(route.name) {
                                popUpTo(AppRoute.HOME.name)
                            }
                        }
                    )
                }
                composable(route = AppRoute.DEVICES.name) {
                    DevicesScreen(
                        paddingValues = paddingValues,
                        onConnectionTypeSelected = {
                            when (it) {
                                DeviceType.BLUETOOTH -> AppRoute.BLUETOOTH_DEVICES
                                DeviceType.BLUETOOTH_LE -> AppRoute.BLUETOOTH_LE_DEVICES
                                DeviceType.DEMO -> AppRoute.DEMO_DEVICES
                                DeviceType.NETWORK -> AppRoute.NETWORK_DEVICES
                                DeviceType.USB -> AppRoute.USB_DEVICES
                                else -> null
                            }?.let { appRoute ->
                                navController.navigate(appRoute.name)
                            }
                        },
                    )
                }
                composable(route = AppRoute.BLUETOOTH_DEVICES.name) {
                    BluetoothDevicesScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.BLUETOOTH_LE_DEVICES.name) {
                    BluetoothLeDevicesScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.DEMO_DEVICES.name) {
                    DemoDevicesScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.NETWORK_DEVICES.name) {
                    NetworkDevicesScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.USB_DEVICES.name) {
                    UsbDevicesScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.SESSION_INFORMATION.name) {
                    SessionInformationScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.DASHBOARD.name) {
                    DashboardScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.DTC.name) {
                    DiagnosticTroubleCodesScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.CURRENT_DATA.name) {
                    CurrentDataScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.FREEZE_FRAME_DATA.name) {
                    FreezeFrameDataScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.VEHICLE_INFORMATION.name) {
                    VehicleInformationScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.TERMINAL.name) {
                    TerminalScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.LOGS.name) {
                    LogsScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.SETTINGS.name) {
                    SettingsScreen(
                        paddingValues = paddingValues,
                    )
                }
            }
        }
    }
}
