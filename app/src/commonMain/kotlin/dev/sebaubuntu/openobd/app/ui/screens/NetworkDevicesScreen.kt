/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.app.ui.composables.FlowResultComposable
import dev.sebaubuntu.openobd.app.viewmodels.NetworkDevicesViewModel
import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import kotlinx.coroutines.flow.Flow
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.cancel
import openobd.app.generated.resources.ic_add
import openobd.app.generated.resources.ic_delete
import openobd.app.generated.resources.ic_edit
import openobd.app.generated.resources.ic_link
import openobd.app.generated.resources.ic_network_node
import openobd.app.generated.resources.network_device_display_name
import openobd.app.generated.resources.network_device_hostname
import openobd.app.generated.resources.network_device_port
import openobd.app.generated.resources.network_devices_connect
import openobd.app.generated.resources.network_devices_create
import openobd.app.generated.resources.network_devices_delete
import openobd.app.generated.resources.network_devices_delete_confirmation
import openobd.app.generated.resources.network_devices_update
import openobd.app.generated.resources.ok
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private sealed interface BottomSheetStatus {
    data object None : BottomSheetStatus
    data object Create : BottomSheetStatus
    data class ItemSelected(val identifier: NetworkDevice.Identifier) : BottomSheetStatus
    data class Edit(val identifier: NetworkDevice.Identifier) : BottomSheetStatus
    data class Delete(val identifier: NetworkDevice.Identifier) : BottomSheetStatus
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkDevicesScreen(
    paddingValues: PaddingValues,
) {
    val networkDevicesViewModel = koinViewModel<NetworkDevicesViewModel>()

    val deviceManagerState by networkDevicesViewModel.state.collectAsStateWithLifecycle()
    val devices by networkDevicesViewModel.devices.collectAsStateWithLifecycle()

    var bottomSheetStatus by remember {
        mutableStateOf<BottomSheetStatus>(BottomSheetStatus.None)
    }

    Box {
        DevicesListComposable(
            paddingValues = paddingValues,
            isToggleable = networkDevicesViewModel.isToggleable,
            deviceManagerState = deviceManagerState,
            devices = devices,
            onToggleManager = networkDevicesViewModel::setState,
            onDeviceSelected = {
                bottomSheetStatus = BottomSheetStatus.ItemSelected(it)
            },
        )

        FloatingActionButton(
            onClick = { bottomSheetStatus = BottomSheetStatus.Create },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_add),
                contentDescription = stringResource(Res.string.network_devices_create),
            )
        }
    }

    NetworkDevicesModalBottomSheet(
        bottomSheetStatus = bottomSheetStatus,
        onChangeStatus = { bottomSheetStatus = it },
        onGetDevice = networkDevicesViewModel::get,
        onSelectDevice = networkDevicesViewModel::selectDevice,
        onCreateDevice = networkDevicesViewModel::createDevice,
        onUpdateDevice = networkDevicesViewModel::updateDevice,
        onDeleteDevice = networkDevicesViewModel::deleteDevice,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NetworkDevicesModalBottomSheet(
    bottomSheetStatus: BottomSheetStatus,
    onChangeStatus: (BottomSheetStatus) -> Unit,
    onGetDevice: (NetworkDevice.Identifier) -> Flow<FlowResult<NetworkDevice, Error>>,
    onSelectDevice: (NetworkDevice.Identifier) -> Unit,
    onCreateDevice: (
        displayName: String,
        hostname: String,
        port: Int,
    ) -> Unit,
    onUpdateDevice: (
        identifier: NetworkDevice.Identifier,
        displayName: String,
        hostname: String,
        port: Int,
    ) -> Unit,
    onDeleteDevice: (
        identifier: NetworkDevice.Identifier,
    ) -> Unit,
) {
    if (bottomSheetStatus == BottomSheetStatus.None) {
        return
    }

    ModalBottomSheet(
        onDismissRequest = { onChangeStatus(BottomSheetStatus.None) },
    ) {
        when (bottomSheetStatus) {
            is BottomSheetStatus.None -> {}

            is BottomSheetStatus.ItemSelected -> {
                val device by onGetDevice(
                    bottomSheetStatus.identifier,
                ).collectAsStateWithLifecycle(FlowResult.Loading())

                FlowResultComposable(
                    flowResult = device,
                    paddingValues = PaddingValues(),
                ) { device ->
                    ListItem(
                        headlineContent = {
                            Text(device.displayName)
                        },
                        supportingContent = {
                            Text("${device.hostname}:${device.port}")
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_network_node),
                                contentDescription = device.displayName,
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                        ),
                    )

                    ListItem(
                        headlineContent = {
                            Text(stringResource(Res.string.network_devices_connect))
                        },
                        modifier = Modifier
                            .clickable {
                                onSelectDevice(device.identifier)
                                onChangeStatus(BottomSheetStatus.None)
                            },
                        leadingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_link),
                                contentDescription = stringResource(
                                    Res.string.network_devices_connect
                                ),
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                        ),
                    )

                    ListItem(
                        headlineContent = {
                            Text(stringResource(Res.string.network_devices_update))
                        },
                        modifier = Modifier
                            .clickable {
                                onChangeStatus(BottomSheetStatus.Edit(device.identifier))
                            },
                        leadingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_edit),
                                contentDescription = stringResource(
                                    Res.string.network_devices_update
                                ),
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                        ),
                    )

                    ListItem(
                        headlineContent = {
                            Text(stringResource(Res.string.network_devices_delete))
                        },
                        modifier = Modifier
                            .clickable {
                                onChangeStatus(BottomSheetStatus.Delete(device.identifier))
                            },
                        leadingContent = {
                            Icon(
                                painter = painterResource(Res.drawable.ic_delete),
                                contentDescription = stringResource(
                                    Res.string.network_devices_delete
                                ),
                            )
                        },
                        colors = ListItemDefaults.colors(
                            containerColor = Color.Transparent,
                        ),
                    )
                }
            }

            is BottomSheetStatus.Create -> {
                var displayName by remember { mutableStateOf("") }
                var hostname by remember { mutableStateOf("") }
                var port by remember { mutableStateOf("35000") }

                TextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    label = {
                        Text(stringResource(Res.string.network_device_display_name))
                    },
                    singleLine = true,
                )

                TextField(
                    value = hostname,
                    onValueChange = { hostname = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    label = {
                        Text(stringResource(Res.string.network_device_hostname))
                    },
                    singleLine = true,
                )

                TextField(
                    value = port,
                    onValueChange = { port = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    label = {
                        Text(stringResource(Res.string.network_device_port))
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                    singleLine = true,
                )

                Button(
                    onClick = {
                        onCreateDevice(
                            displayName,
                            hostname,
                            port.toIntOrNull() ?: return@Button,
                        )
                        onChangeStatus(BottomSheetStatus.None)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    Text(stringResource(Res.string.network_devices_create))
                }
            }

            is BottomSheetStatus.Edit -> {
                val device by onGetDevice(
                    bottomSheetStatus.identifier,
                ).collectAsStateWithLifecycle(FlowResult.Loading())

                FlowResultComposable(
                    flowResult = device,
                    paddingValues = PaddingValues(),
                ) { device ->
                    var displayName by remember { mutableStateOf(device.displayName) }
                    var hostname by remember { mutableStateOf(device.hostname) }
                    var port by remember { mutableStateOf(device.port.toString()) }

                    TextField(
                        value = displayName,
                        onValueChange = { displayName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        label = {
                            Text(stringResource(Res.string.network_device_display_name))
                        },
                        singleLine = true,
                    )

                    TextField(
                        value = hostname,
                        onValueChange = { hostname = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        label = {
                            Text(stringResource(Res.string.network_device_hostname))
                        },
                        singleLine = true,
                    )

                    TextField(
                        value = port,
                        onValueChange = { port = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        label = {
                            Text(stringResource(Res.string.network_device_port))
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                        singleLine = true,
                    )

                    Button(
                        onClick = {
                            onUpdateDevice(
                                bottomSheetStatus.identifier,
                                displayName,
                                hostname,
                                port.toIntOrNull() ?: return@Button,
                            )
                            onChangeStatus(BottomSheetStatus.None)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        Text(stringResource(Res.string.network_devices_update))
                    }
                }
            }

            is BottomSheetStatus.Delete -> {
                Text(
                    text = stringResource(Res.string.network_devices_delete_confirmation),
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Button(
                        onClick = {
                            onDeleteDevice(bottomSheetStatus.identifier)
                            onChangeStatus(BottomSheetStatus.None)
                        },
                        modifier = Modifier.weight(1f).padding(8.dp),
                        colors = ButtonDefaults.buttonColors().copy(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                    ) {
                        Text(stringResource(Res.string.ok))
                    }

                    Button(
                        onClick = {
                            onChangeStatus(BottomSheetStatus.None)
                        },
                        modifier = Modifier.weight(1f).padding(8.dp),
                    ) {
                        Text(stringResource(Res.string.cancel))
                    }
                }
            }
        }
    }
}
