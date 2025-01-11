/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.apps

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.getOrNull
import dev.sebaubuntu.openobd.models.ConnectionType
import dev.sebaubuntu.openobd.models.Device
import dev.sebaubuntu.openobd.obd.Elm327Manager
import dev.sebaubuntu.openobd.repositories.BluetoothRepository
import dev.sebaubuntu.openobd.repositories.LoggingRepository
import dev.sebaubuntu.openobd.repositories.NetworkRepository
import dev.sebaubuntu.openobd.repositories.ObdRepository
import dev.sebaubuntu.openobd.ui.AppRoute
import dev.sebaubuntu.openobd.ui.LocalBluetoothManager
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
import dev.sebaubuntu.openobd.ui.screens.VehicleInformationScreen
import dev.sebaubuntu.openobd.ui.themes.OpenOBDTheme
import dev.sebaubuntu.openobd.utils.NetworkManager
import dev.sebaubuntu.openobd.viewmodels.CurrentDeviceViewModel
import kotlinx.coroutines.CoroutineScope
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.app_name
import openobd.app.generated.resources.connection_type_none
import openobd.app.generated.resources.go_back
import openobd.app.generated.resources.ic_device_unknown
import openobd.app.generated.resources.no_device_connected
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/**
 * App entry point.
 */
@Composable
fun App(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {
    // Data
    val bluetoothManager = LocalBluetoothManager.current
    val elm327Manager = remember { Elm327Manager() }
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
    val obdRepository = remember {
        ObdRepository(
            coroutineScope = coroutineScope,
            bluetoothRepository = bluetoothRepository,
            networkRepository = networkRepository,
            elm327Manager = elm327Manager,
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
        obdRepository = obdRepository,
        loggingRepository = loggingRepository,
    )
}

@Composable
private fun App(
    bluetoothRepository: BluetoothRepository,
    networkRepository: NetworkRepository,
    obdRepository: ObdRepository,
    loggingRepository: LoggingRepository,
) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = AppRoute.valueOf(
        backStackEntry?.destination?.route ?: AppRoute.HOME.name
    )

    val currentDeviceViewModel = viewModel {
        CurrentDeviceViewModel(
            obdRepository = obdRepository,
        )
    }

    val device by currentDeviceViewModel.device.collectAsStateWithLifecycle()

    OpenOBDTheme {
        Scaffold(
            topBar = {
                OpenOBDTopAppBar(
                    currentRoute = currentRoute,
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateUp = { navController.navigateUp() },
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
                                ConnectionType.BLUETOOTH -> AppRoute.BLUETOOTH_DEVICES
                                ConnectionType.NETWORK -> AppRoute.NETWORK_DEVICES
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
                        obdRepository = obdRepository,
                    )
                }
                composable(route = AppRoute.NETWORK_DEVICES.name) {
                    NetworkDevicesScreen(
                        paddingValues = paddingValues,
                        networkRepository = networkRepository,
                        obdRepository = obdRepository,
                    )
                }
                composable(route = AppRoute.DASHBOARD.name) {
                    DashboardScreen(
                        paddingValues = paddingValues,
                        obdRepository = obdRepository,
                    )
                }
                composable(route = AppRoute.DTC.name) {
                    DiagnosticTroubleCodesScreen(
                        paddingValues = paddingValues,
                        obdRepository = obdRepository,
                    )
                }
                composable(route = AppRoute.CURRENT_DATA.name) {
                    CurrentDataScreen(
                        paddingValues = paddingValues,
                        obdRepository = obdRepository,
                    )
                }
                composable(route = AppRoute.FREEZE_FRAME_DATA.name) {
                    FreezeFrameDataScreen(
                        paddingValues = paddingValues,
                        obdRepository = obdRepository,
                    )
                }
                composable(route = AppRoute.VEHICLE_INFORMATION.name) {
                    VehicleInformationScreen(
                        paddingValues = paddingValues,
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

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun OpenOBDTopAppBar(
    currentRoute: AppRoute,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Text(stringResource(currentRoute.title))
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(Res.string.go_back)
                    )
                }
            }
        }
    )
}

@Composable
private fun OpenOBDBottomBar(
    onNavigateToHome: () -> Unit,
    onNavigateToDevices: () -> Unit,
    device: Device?,
) {
    BottomAppBar {
        IconButton(
            onClick = onNavigateToHome,
        ) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = stringResource(Res.string.app_name),
            )
        }

        Spacer(Modifier.weight(1f, true))

        Button(
            onClick = onNavigateToDevices,
            modifier = Modifier.padding(end = 16.dp),
        ) {
            Icon(
                painter = painterResource(
                    device?.connectionType?.drawableResource
                        ?: Res.drawable.ic_device_unknown
                ),
                contentDescription = stringResource(
                    device?.connectionType?.stringResource
                        ?: Res.string.connection_type_none
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = device?.displayName ?: stringResource(Res.string.no_device_connected),
                )
                device?.let {
                    Text(
                        text = stringResource(it.connectionType.stringResource),
                    )
                }
            }
        }
    }
}
