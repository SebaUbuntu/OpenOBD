/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.backend.models.DemoDevice
import dev.sebaubuntu.openobd.viewmodels.DemoDevicesViewModel
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.connection_type_demo
import openobd.app.generated.resources.ic_simulation
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DemoDevicesScreen(
    paddingValues: PaddingValues,
) {
    val demoDevicesViewModel = koinViewModel<DemoDevicesViewModel>()

    val demoDevices by demoDevicesViewModel.devices.collectAsStateWithLifecycle()

    DemoDevicesScreen(
        paddingValues = paddingValues,
        demoDevices = demoDevices,
        onDeviceSelected = demoDevicesViewModel::selectDevice,
    )
}

@Composable
private fun DemoDevicesScreen(
    paddingValues: PaddingValues,
    demoDevices: List<DemoDevice>,
    onDeviceSelected: (DemoDevice.Identifier) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        items(demoDevices) {
            DemoDeviceListItem(
                demoDevice = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDeviceSelected(it.identifier) },
            )
        }
    }
}

@Composable
private fun DemoDeviceListItem(
    demoDevice: DemoDevice,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text = demoDevice.displayName,
            )
        },
        modifier = modifier,
        supportingContent = {
            Text(
                text = demoDevice.displayName,
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_simulation),
                contentDescription = stringResource(Res.string.connection_type_demo),
            )
        },
    )
}
