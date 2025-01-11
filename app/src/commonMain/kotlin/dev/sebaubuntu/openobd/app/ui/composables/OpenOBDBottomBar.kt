/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sebaubuntu.openobd.app.ext.drawableResource
import dev.sebaubuntu.openobd.app.ext.stringResource
import dev.sebaubuntu.openobd.app.models.ConnectionStatus
import dev.sebaubuntu.openobd.backend.models.Device
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.app_name
import openobd.app.generated.resources.connection_type_none
import openobd.app.generated.resources.ic_device_unknown
import openobd.app.generated.resources.ic_menu
import openobd.app.generated.resources.no_device_connected
import openobd.app.generated.resources.unknown_device
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun OpenOBDBottomBar(
    onNavigateToHome: () -> Unit,
    onNavigateToDevices: () -> Unit,
    device: Device<*>?,
    connectionStatus: ConnectionStatus,
) {
    BottomAppBar {
        IconButton(
            onClick = onNavigateToHome,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_menu),
                contentDescription = stringResource(Res.string.app_name),
            )
        }

        Spacer(Modifier.weight(1f, true))

        Button(
            onClick = onNavigateToDevices,
            modifier = Modifier.padding(end = 16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(
                        device?.identifier?.deviceType?.drawableResource
                            ?: Res.drawable.ic_device_unknown
                    ),
                    contentDescription = stringResource(
                        device?.identifier?.deviceType?.stringResource
                            ?: Res.string.connection_type_none
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(
                    modifier = Modifier.size(16.dp),
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = device?.let {
                            it.displayName ?: stringResource(Res.string.unknown_device)
                        } ?: stringResource(Res.string.no_device_connected),
                    )

                    device?.let {
                        Text(
                            text = stringResource(it.identifier.deviceType.stringResource),
                        )
                    }
                }
            }
        }
    }
}
