/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.apps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.sebaubuntu.openobd.backend.models.DeviceType
import dev.sebaubuntu.openobd.backend.network.NetworkManager
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.getOrNull
import dev.sebaubuntu.openobd.repositories.BluetoothRepository
import dev.sebaubuntu.openobd.repositories.ConnectionStatusRepository
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.repositories.LoggingRepository
import dev.sebaubuntu.openobd.repositories.NetworkRepository
import dev.sebaubuntu.openobd.repositories.ObdRepository
import dev.sebaubuntu.openobd.ui.AppRoute
import dev.sebaubuntu.openobd.ui.LocalBluetoothManager
import dev.sebaubuntu.openobd.ui.composables.OpenOBDBottomBar
import dev.sebaubuntu.openobd.ui.composables.OpenOBDTopAppBar
import dev.sebaubuntu.openobd.ui.screens.BluetoothDevicesScreen
import dev.sebaubuntu.openobd.ui.screens.CurrentDataScreen
import dev.sebaubuntu.openobd.ui.screens.DashboardScreen
import dev.sebaubuntu.openobd.ui.screens.DevicesScreen
import dev.sebaubuntu.openobd.ui.screens.DiagnosticTroubleCodesScreen
import dev.sebaubuntu.openobd.ui.screens.FreezeFrameDataScreen
import dev.sebaubuntu.openobd.ui.screens.HomeScreen
import dev.sebaubuntu.openobd.ui.screens.LogsScreen
import dev.sebaubuntu.openobd.ui.screens.NetworkDevicesScreen
import dev.sebaubuntu.openobd.ui.screens.SettingsScreen
import dev.sebaubuntu.openobd.ui.screens.TerminalScreen
import dev.sebaubuntu.openobd.ui.screens.VehicleInformationScreen
import dev.sebaubuntu.openobd.ui.themes.OpenOBDTheme
import dev.sebaubuntu.openobd.viewmodels.CurrentDeviceViewModel
import kotlinx.coroutines.CoroutineScope

/**
 * App entry point.
 */
@Composable
fun App(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    // Data
    val bluetoothManager = LocalBluetoothManager.current
    val networkManager = remember { NetworkManager() }

    // Repositories
    val bluetoothRepository = remember {
        BluetoothRepository(
            coroutineScope = coroutineScope,
            bluetoothManager = bluetoothManager,
        )
    }
    val networkRepository = remember {
        NetworkRepository(
            coroutineScope = coroutineScope,
            networkManager = networkManager,
        )
    }
    val deviceConnectionRepository = remember {
        DeviceConnectionRepository(
            coroutineScope = coroutineScope,
            bluetoothRepository = bluetoothRepository,
            networkRepository = networkRepository,
        )
    }
    val obdRepository = remember {
        ObdRepository(
            coroutineScope = coroutineScope,
            deviceConnectionRepository = deviceConnectionRepository,
        )
    }
    val connectionStatusRepository = remember {
        ConnectionStatusRepository(
            coroutineScope = coroutineScope,
            deviceConnectionRepository = deviceConnectionRepository,
            obdRepository = obdRepository,
        )
    }
    val loggingRepository = remember {
        LoggingRepository(
            coroutineScope = coroutineScope,
        )
    }

    App(
        bluetoothRepository = bluetoothRepository,
        networkRepository = networkRepository,
        deviceConnectionRepository = deviceConnectionRepository,
        obdRepository = obdRepository,
        connectionStatusRepository = connectionStatusRepository,
        loggingRepository = loggingRepository,
    )
}

@Composable
private fun App(
    bluetoothRepository: BluetoothRepository,
    networkRepository: NetworkRepository,
    deviceConnectionRepository: DeviceConnectionRepository,
    obdRepository: ObdRepository,
    connectionStatusRepository: ConnectionStatusRepository,
    loggingRepository: LoggingRepository,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = AppRoute.valueOf(
        backStackEntry?.destination?.route ?: AppRoute.HOME.name
    )

    val currentDeviceViewModel = viewModel {
        CurrentDeviceViewModel(
            deviceConnectionRepository = deviceConnectionRepository,
        )
    }

    val device by currentDeviceViewModel.device.collectAsStateWithLifecycle()
    val connectionStatus by connectionStatusRepository.connectionStatus.collectAsStateWithLifecycle()

    OpenOBDTheme {
        Scaffold(
            topBar = {
                OpenOBDTopAppBar(
                    currentRoute = currentRoute,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
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
                                DeviceType.NETWORK -> AppRoute.NETWORK_DEVICES
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
                        bluetoothRepository = bluetoothRepository,
                        deviceConnectionRepository = deviceConnectionRepository,
                    )
                }
                composable(route = AppRoute.NETWORK_DEVICES.name) {
                    NetworkDevicesScreen(
                        paddingValues = paddingValues,
                        networkRepository = networkRepository,
                        deviceConnectionRepository = deviceConnectionRepository,
                    )
                }
                composable(route = AppRoute.DASHBOARD.name) {
                    DashboardScreen(
                        paddingValues = paddingValues,
                        connectionStatusRepository = connectionStatusRepository,
                        obdRepository = obdRepository,
                    )
                }
                composable(route = AppRoute.DTC.name) {
                    DiagnosticTroubleCodesScreen(
                        paddingValues = paddingValues,
                        connectionStatusRepository = connectionStatusRepository,
                        obdRepository = obdRepository,
                    )
                }
                composable(route = AppRoute.CURRENT_DATA.name) {
                    CurrentDataScreen(
                        paddingValues = paddingValues,
                        connectionStatusRepository = connectionStatusRepository,
                        obdRepository = obdRepository,
                    )
                }
                composable(route = AppRoute.FREEZE_FRAME_DATA.name) {
                    FreezeFrameDataScreen(
                        paddingValues = paddingValues,
                        connectionStatusRepository = connectionStatusRepository,
                        obdRepository = obdRepository,
                    )
                }
                composable(route = AppRoute.VEHICLE_INFORMATION.name) {
                    VehicleInformationScreen(
                        paddingValues = paddingValues,
                        connectionStatusRepository = connectionStatusRepository,
                        obdRepository = obdRepository,
                    )
                }
                composable(route = AppRoute.TERMINAL.name) {
                    TerminalScreen(
                        paddingValues = paddingValues,
                        connectionStatusRepository = connectionStatusRepository,
                        obdRepository = obdRepository,
                    )
                }
                composable(route = AppRoute.LOGS.name) {
                    LogsScreen(
                        paddingValues = paddingValues,
                        loggingRepository = loggingRepository,
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
