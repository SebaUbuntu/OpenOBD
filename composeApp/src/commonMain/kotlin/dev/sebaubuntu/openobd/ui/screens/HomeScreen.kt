/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sebaubuntu.openobd.ui.AppRoute
import openobd.composeapp.generated.resources.Res
import openobd.composeapp.generated.resources.ic_sensors
import openobd.composeapp.generated.resources.ic_settings
import openobd.composeapp.generated.resources.ic_speed
import openobd.composeapp.generated.resources.ic_warning
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private enum class Tab(
    val route: AppRoute,
    val drawableResource: DrawableResource,
) {
    DASHBOARD(
        AppRoute.DASHBOARD,
        Res.drawable.ic_speed,
    ),
    DTC(
        AppRoute.DTC,
        Res.drawable.ic_warning,
    ),
    SENSORS(
        AppRoute.SENSORS,
        Res.drawable.ic_sensors,
    ),
    SETTINGS(
        AppRoute.SETTINGS,
        Res.drawable.ic_settings,
    );

    val stringResource = route.title
}

/**
 * Home page screen.
 */
@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    onNavigateTo: (AppRoute) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(Tab.entries) {
            Card(
                onClick = { onNavigateTo(it.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(it.drawableResource),
                        contentDescription = stringResource(it.stringResource),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp),
                    )
                    Text(
                        text = stringResource(it.stringResource),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
