/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sebaubuntu.openobd.app.ui.LocalNavBackStack
import dev.sebaubuntu.openobd.app.ui.navigation.AppNavRoute
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.current_data
import openobd.app.generated.resources.dashboard
import openobd.app.generated.resources.dtc
import openobd.app.generated.resources.freeze_frame_data
import openobd.app.generated.resources.ic_laptop_car
import openobd.app.generated.resources.ic_list_alt
import openobd.app.generated.resources.ic_sensors
import openobd.app.generated.resources.ic_settings
import openobd.app.generated.resources.ic_speed
import openobd.app.generated.resources.ic_terminal
import openobd.app.generated.resources.ic_timer
import openobd.app.generated.resources.ic_warning
import openobd.app.generated.resources.logs
import openobd.app.generated.resources.settings
import openobd.app.generated.resources.terminal
import openobd.app.generated.resources.vehicle_information
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private enum class Tab(
    val route: AppNavRoute,
    val stringResource: StringResource,
    val drawableResource: DrawableResource,
) {
    DASHBOARD(
        AppNavRoute.Dashboard,
        Res.string.dashboard,
        Res.drawable.ic_speed,
    ),
    DTC(
        AppNavRoute.DiagnosticTroubleCodes,
        Res.string.dtc,
        Res.drawable.ic_warning,
    ),
    CURRENT_DATA(
        AppNavRoute.CurrentData,
        Res.string.current_data,
        Res.drawable.ic_sensors,
    ),
    FREEZE_FRAME_DATA(
        AppNavRoute.FreezeFrameData,
        Res.string.freeze_frame_data,
        Res.drawable.ic_timer,
    ),
    VEHICLE_INFORMATION(
        AppNavRoute.VehicleInformation,
        Res.string.vehicle_information,
        Res.drawable.ic_laptop_car,
    ),
    TERMINAL(
        AppNavRoute.Terminal,
        Res.string.terminal,
        Res.drawable.ic_terminal,
    ),
    LOGS(
        AppNavRoute.Logs,
        Res.string.logs,
        Res.drawable.ic_list_alt,
    ),
    SETTINGS(
        AppNavRoute.Settings,
        Res.string.settings,
        Res.drawable.ic_settings,
    ),
}

/**
 * Home page screen.
 */
@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
) {
    val navBackStack = LocalNavBackStack.current

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(minSize = 128.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
    ) {
        items(
            items = Tab.entries,
            key = Tab::ordinal,
        ) {
            TabCard(
                tab = it,
                onClick = { navBackStack.add(it.route) },
                modifier = Modifier.padding(8.dp),
            )
        }
    }
}

@Composable
private fun TabCard(
    tab: Tab,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(tab.drawableResource),
                contentDescription = stringResource(tab.stringResource),
                modifier = Modifier.size(36.dp),
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(tab.stringResource),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    }
}
