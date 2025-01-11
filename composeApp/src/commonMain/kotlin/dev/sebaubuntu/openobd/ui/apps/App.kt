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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.sebaubuntu.openobd.models.ConnectionType
import dev.sebaubuntu.openobd.models.FlowResult.Companion.getOrNull
import dev.sebaubuntu.openobd.models.ObdDevice
import dev.sebaubuntu.openobd.repositories.BluetoothRepository
import dev.sebaubuntu.openobd.repositories.DevicesRepository
import dev.sebaubuntu.openobd.ui.AppRoute
import dev.sebaubuntu.openobd.ui.LocalBluetoothManager
import dev.sebaubuntu.openobd.ui.screens.BluetoothDevicesScreen
import dev.sebaubuntu.openobd.ui.screens.DashboardScreen
import dev.sebaubuntu.openobd.ui.screens.DevicesScreen
import dev.sebaubuntu.openobd.ui.screens.DtcScreen
import dev.sebaubuntu.openobd.ui.screens.HomeScreen
import dev.sebaubuntu.openobd.ui.screens.SensorsScreen
import dev.sebaubuntu.openobd.ui.screens.SettingsScreen
import dev.sebaubuntu.openobd.ui.themes.OpenOBDTheme
import dev.sebaubuntu.openobd.viewmodels.CurrentDeviceViewModel
import kotlinx.coroutines.CoroutineScope
import openobd.composeapp.generated.resources.Res
import openobd.composeapp.generated.resources.app_name
import openobd.composeapp.generated.resources.connection_type_none
import openobd.composeapp.generated.resources.go_back
import openobd.composeapp.generated.resources.ic_device_unknown
import openobd.composeapp.generated.resources.no_device_connected
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun App(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
) {
    // Data
    val bluetoothManager = LocalBluetoothManager.current

    // Repositories
    val bluetoothRepository = remember {
        BluetoothRepository(
            coroutineScope = coroutineScope,
            bluetoothManager = bluetoothManager,
        )
    }
    val devicesRepository = remember {
        DevicesRepository(
            coroutineScope = coroutineScope,
            bluetoothRepository = bluetoothRepository,
        )
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = AppRoute.valueOf(
        backStackEntry?.destination?.route ?: AppRoute.HOME.name
    )

    val currentDeviceViewModel = viewModel {
        CurrentDeviceViewModel(
            devicesRepository = devicesRepository,
        )
    }

    val obdDevice by currentDeviceViewModel.obdDevice.collectAsStateWithLifecycle()

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
                    obdDevice = obdDevice.getOrNull(),
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
                        devicesRepository = devicesRepository,
                    )
                }
                composable(route = AppRoute.DASHBOARD.name) {
                    DashboardScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.DTC.name) {
                    DtcScreen(
                        paddingValues = paddingValues,
                    )
                }
                composable(route = AppRoute.SENSORS.name) {
                    SensorsScreen(
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
    obdDevice: ObdDevice?,
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
                    obdDevice?.connectionType?.drawableResource
                        ?: Res.drawable.ic_device_unknown
                ),
                contentDescription = stringResource(
                    obdDevice?.connectionType?.stringResource
                        ?: Res.string.connection_type_none
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = obdDevice?.name ?: stringResource(Res.string.no_device_connected),
                )
                obdDevice?.let {
                    Text(
                        text = stringResource(it.connectionType.stringResource),
                    )
                }
            }
        }
    }
}
