/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothManager
import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothSearchState
import dev.sebaubuntu.openobd.backend.models.BluetoothDevice
import dev.sebaubuntu.openobd.models.Permission
import dev.sebaubuntu.openobd.ui.composables.PermissionGatedComposable
import dev.sebaubuntu.openobd.viewmodels.BluetoothDevicesViewModel
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.bluetooth_device_state_available
import openobd.app.generated.resources.bluetooth_device_state_bonded
import openobd.app.generated.resources.bluetooth_device_state_bonding
import openobd.app.generated.resources.bluetooth_device_state_connected
import openobd.app.generated.resources.bluetooth_device_state_connecting
import openobd.app.generated.resources.bluetooth_device_state_disconnecting
import openobd.app.generated.resources.bluetooth_device_state_unknown
import openobd.app.generated.resources.bluetooth_state
import openobd.app.generated.resources.bluetooth_state_disabled
import openobd.app.generated.resources.bluetooth_state_disabling
import openobd.app.generated.resources.bluetooth_state_enabled
import openobd.app.generated.resources.bluetooth_state_enabling
import openobd.app.generated.resources.bluetooth_state_unavailable
import openobd.app.generated.resources.connection_type_bluetooth
import openobd.app.generated.resources.ic_bluetooth
import openobd.app.generated.resources.unknown_device
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * Bluetooth device selector screen.
 */
@Composable
fun BluetoothDevicesScreen(
    paddingValues: PaddingValues,
) {
    PermissionGatedComposable(
        permission = Permission.BLUETOOTH,
        paddingValues = paddingValues,
    ) {
        val bluetoothDevicesViewModel = koinViewModel<BluetoothDevicesViewModel>()

        val bluetoothState by bluetoothDevicesViewModel.state.collectAsStateWithLifecycle()
        val bluetoothDiscover by bluetoothDevicesViewModel.discover.collectAsStateWithLifecycle()

        BluetoothDevicesScreen(
            paddingValues = paddingValues,
            bluetoothState = bluetoothState,
            bluetoothSearchState = bluetoothDiscover,
            onToggleBluetooth = bluetoothDevicesViewModel::toggleBluetooth,
            onDeviceSelected = bluetoothDevicesViewModel::selectDevice,
        )
    }
}

@Composable
private fun BluetoothDevicesScreen(
    paddingValues: PaddingValues,
    bluetoothState: BluetoothManager.State,
    bluetoothSearchState: BluetoothSearchState,
    onToggleBluetooth: (Boolean) -> Unit,
    onDeviceSelected: (BluetoothDevice.Identifier) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        if (bluetoothSearchState.isSearching) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
        ) {
            item {
                BluetoothStateLayout(
                    bluetoothState = bluetoothState,
                    onToggleBluetooth = onToggleBluetooth,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            items(items = bluetoothSearchState.devices) {
                BluetoothDeviceListItem(
                    bluetoothDevice = it,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDeviceSelected(it.identifier) },
                )
            }
        }
    }
}

@Composable
private fun BluetoothStateLayout(
    bluetoothState: BluetoothManager.State,
    onToggleBluetooth: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(
                Res.string.bluetooth_state,
                stringResource(
                    when (bluetoothState) {
                        BluetoothManager.State.UNAVAILABLE -> Res.string.bluetooth_state_unavailable
                        BluetoothManager.State.DISABLED -> Res.string.bluetooth_state_disabled
                        BluetoothManager.State.DISABLING -> Res.string.bluetooth_state_disabling
                        BluetoothManager.State.ENABLING -> Res.string.bluetooth_state_enabling
                        BluetoothManager.State.ENABLED -> Res.string.bluetooth_state_enabled
                    }
                ),
            ),
            modifier = Modifier
                .padding(start = 16.dp, end = 8.dp)
                .weight(1f),
        )
        Switch(
            checked = when (bluetoothState) {
                BluetoothManager.State.UNAVAILABLE -> false
                BluetoothManager.State.DISABLED -> false
                BluetoothManager.State.DISABLING -> true
                BluetoothManager.State.ENABLING -> false
                BluetoothManager.State.ENABLED -> true
            },
            onCheckedChange = when (bluetoothState) {
                BluetoothManager.State.UNAVAILABLE -> null
                BluetoothManager.State.DISABLED -> {
                    { onToggleBluetooth(true) }
                }

                BluetoothManager.State.DISABLING -> null
                BluetoothManager.State.ENABLING -> null
                BluetoothManager.State.ENABLED -> {
                    { onToggleBluetooth(false) }
                }
            },
            modifier = Modifier.padding(end = 16.dp),
        )
    }
}

@Composable
private fun BluetoothDeviceListItem(
    bluetoothDevice: BluetoothDevice,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text = bluetoothDevice.displayName ?: stringResource(Res.string.unknown_device),
            )
        },
        modifier = modifier,
        overlineContent = {
            Text(
                text = stringResource(
                    when (bluetoothDevice.state) {
                        BluetoothDevice.State.UNKNOWN -> Res.string.bluetooth_device_state_unknown
                        BluetoothDevice.State.AVAILABLE ->
                            Res.string.bluetooth_device_state_available

                        BluetoothDevice.State.BONDING -> Res.string.bluetooth_device_state_bonding
                        BluetoothDevice.State.BONDED -> Res.string.bluetooth_device_state_bonded
                        BluetoothDevice.State.CONNECTING ->
                            Res.string.bluetooth_device_state_connecting

                        BluetoothDevice.State.CONNECTED ->
                            Res.string.bluetooth_device_state_connected

                        BluetoothDevice.State.DISCONNECTING ->
                            Res.string.bluetooth_device_state_disconnecting
                    }
                ),
            )
        },
        supportingContent = {
            Text(
                text = bluetoothDevice.identifier.macAddress,
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_bluetooth),
                contentDescription = stringResource(
                    Res.string.connection_type_bluetooth
                ),
            )
        },
    )
}
