/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.apps

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.sebaubuntu.openobd.app.repositories.ConnectionStatusRepository
import dev.sebaubuntu.openobd.app.ui.composables.OpenOBDBottomBar
import dev.sebaubuntu.openobd.app.ui.composables.OpenOBDTopAppBar
import dev.sebaubuntu.openobd.app.ui.navigation.AppNavRoute
import dev.sebaubuntu.openobd.app.ui.navigation.OpenOBDNavDisplay
import dev.sebaubuntu.openobd.app.ui.themes.OpenOBDTheme
import dev.sebaubuntu.openobd.app.viewmodels.AppViewModel
import dev.sebaubuntu.openobd.app.viewmodels.CurrentDeviceViewModel
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.getOrNull
import kotlinx.serialization.serializer
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * App entry point.
 */
@Composable
fun App() {
    val navBackStack = rememberNavBackStack<AppNavRoute>(AppNavRoute.Home)

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
                    navBackStack = navBackStack,
                    connectionStatus = connectionStatus,
                )
            },
            bottomBar = {
                OpenOBDBottomBar(
                    navBackStack = navBackStack,
                    device = device.getOrNull(),
                    connectionStatus = connectionStatus,
                )
            },
        ) { paddingValues ->
            OpenOBDNavDisplay(
                paddingValues = paddingValues,
                navBackStack = navBackStack,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private inline fun <reified T : NavKey> rememberNavBackStack(
    vararg elements: T,
) = rememberSerializable(serializer = serializer()) {
    NavBackStack(*elements)
}
