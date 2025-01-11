/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sebaubuntu.openobd.app.models.ConnectionStatus
import dev.sebaubuntu.openobd.app.ui.AppRoute
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.connection_status_connecting
import openobd.app.generated.resources.connection_status_failed_connection
import openobd.app.generated.resources.connection_status_failed_initialization
import openobd.app.generated.resources.connection_status_idle
import openobd.app.generated.resources.connection_status_initializing
import openobd.app.generated.resources.connection_status_ready
import openobd.app.generated.resources.go_back
import openobd.app.generated.resources.ic_arrow_back
import openobd.app.generated.resources.ic_car_crash
import openobd.app.generated.resources.ic_check
import openobd.app.generated.resources.ic_link_off
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun OpenOBDTopAppBar(
    currentRoute: AppRoute,
    canNavigateBack: Boolean,
    onNavigateUp: () -> Unit,
    onNavigateToSessionInformation: () -> Unit,
    connectionStatus: ConnectionStatus,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(currentRoute.title),
                    modifier = Modifier.weight(1f),
                )

                Box(
                    modifier = Modifier.padding(16.dp),
                    contentAlignment = Alignment.CenterEnd,
                ) {
                    Card {
                        Row(
                            modifier = Modifier
                                .clickable { onNavigateToSessionInformation() }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            when (connectionStatus) {
                                ConnectionStatus.IDLE -> {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_link_off),
                                        contentDescription = stringResource(
                                            Res.string.connection_status_failed_connection
                                        ),
                                    )
                                }

                                ConnectionStatus.CONNECTING,
                                ConnectionStatus.INITIALIZING -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = LocalContentColor.current,
                                        strokeWidth = 2.dp,
                                    )
                                }

                                ConnectionStatus.READY -> {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_check),
                                        contentDescription = null,
                                    )
                                }

                                ConnectionStatus.FAILED_CONNECTION -> {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_link_off),
                                        contentDescription = stringResource(
                                            Res.string.connection_status_failed_connection
                                        ),
                                    )
                                }

                                ConnectionStatus.FAILED_INITIALIZATION -> {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_car_crash),
                                        contentDescription = stringResource(
                                            Res.string.connection_status_failed_connection
                                        ),
                                    )
                                }
                            }

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = stringResource(
                                    when (connectionStatus) {
                                        ConnectionStatus.IDLE ->
                                            Res.string.connection_status_idle

                                        ConnectionStatus.CONNECTING ->
                                            Res.string.connection_status_connecting

                                        ConnectionStatus.INITIALIZING ->
                                            Res.string.connection_status_initializing

                                        ConnectionStatus.READY ->
                                            Res.string.connection_status_ready

                                        ConnectionStatus.FAILED_CONNECTION ->
                                            Res.string.connection_status_failed_connection

                                        ConnectionStatus.FAILED_INITIALIZATION ->
                                            Res.string.connection_status_failed_initialization
                                    }
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = onNavigateUp) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_back),
                        contentDescription = stringResource(Res.string.go_back)
                    )
                }
            }
        }
    )
}
