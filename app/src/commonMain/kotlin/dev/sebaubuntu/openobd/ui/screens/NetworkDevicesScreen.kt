/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.viewmodels.NetworkDevicesViewModel
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.connection_type_network
import openobd.app.generated.resources.ic_network_node
import openobd.app.generated.resources.unknown_device
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NetworkDevicesScreen(
    paddingValues: PaddingValues,
) {
    val networkDevicesViewModel = koinViewModel<NetworkDevicesViewModel>()

    val networkDevices = networkDevicesViewModel.devices.collectAsStateWithLifecycle()

    NetworkDevicesScreen(
        paddingValues = paddingValues,
        networkDevices = networkDevices.value,
        onDeviceSelected = networkDevicesViewModel::selectDevice,
    )
}

@Composable
private fun NetworkDevicesScreen(
    paddingValues: PaddingValues,
    networkDevices: List<NetworkDevice>,
    onDeviceSelected: (NetworkDevice.Identifier) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            items(items = networkDevices) {
                NetworkDeviceListItem(
                    networkDevice = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDeviceSelected(it.identifier) },
                )
            }
        }
    }
}

@Composable
private fun NetworkDeviceListItem(
    networkDevice: NetworkDevice,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text = networkDevice.displayName ?: stringResource(Res.string.unknown_device),
            )
        },
        modifier = modifier,
        supportingContent = {
            Text(
                text = "${networkDevice.hostname}:${networkDevice.port}",
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_network_node),
                contentDescription = stringResource(Res.string.connection_type_network),
            )
        },
    )
}
