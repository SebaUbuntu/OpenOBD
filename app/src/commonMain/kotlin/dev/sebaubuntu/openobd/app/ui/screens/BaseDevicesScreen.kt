/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.app.ext.drawableResource
import dev.sebaubuntu.openobd.app.ext.stringResource
import dev.sebaubuntu.openobd.app.models.Permission
import dev.sebaubuntu.openobd.app.ui.composables.FlowResultComposable
import dev.sebaubuntu.openobd.app.ui.composables.PermissionGatedComposable
import dev.sebaubuntu.openobd.app.viewmodels.BaseDevicesViewModel
import dev.sebaubuntu.openobd.backend.models.BluetoothDevice
import dev.sebaubuntu.openobd.backend.models.BluetoothLeDevice
import dev.sebaubuntu.openobd.backend.models.DemoDevice
import dev.sebaubuntu.openobd.backend.models.Device
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.backend.models.DevicesState
import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.backend.models.UsbDevice
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.bluetooth_device_state_available
import openobd.app.generated.resources.bluetooth_device_state_bonded
import openobd.app.generated.resources.bluetooth_device_state_bonding
import openobd.app.generated.resources.bluetooth_device_state_connected
import openobd.app.generated.resources.bluetooth_device_state_connecting
import openobd.app.generated.resources.bluetooth_device_state_disconnecting
import openobd.app.generated.resources.bluetooth_device_state_unknown
import openobd.app.generated.resources.device_manager_state_disabled
import openobd.app.generated.resources.device_manager_state_disabling
import openobd.app.generated.resources.device_manager_state_enabled
import openobd.app.generated.resources.device_manager_state_enabling
import openobd.app.generated.resources.device_manager_state_unavailable
import openobd.app.generated.resources.unknown_device
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
inline fun <reified VM : BaseDevicesViewModel<D, ID>,
        D : Device<ID>, ID : Device.Identifier> BaseDevicesScreen(
    paddingValues: PaddingValues,
    permission: Permission?,
) {
    val content = @Composable {
        val baseDevicesViewModel = koinViewModel<VM>()

        val deviceManagerState by baseDevicesViewModel.state.collectAsStateWithLifecycle()
        val devices by baseDevicesViewModel.devices.collectAsStateWithLifecycle()

        DevicesListComposable(
            paddingValues = paddingValues,
            isToggleable = baseDevicesViewModel.isToggleable,
            deviceManagerState = deviceManagerState,
            devices = devices,
            onToggleManager = baseDevicesViewModel::setState,
            onDeviceSelected = baseDevicesViewModel::selectDevice,
        )
    }

    permission?.also {
        PermissionGatedComposable(
            permission = it,
            paddingValues = paddingValues,
        ) {
            content()
        }
    } ?: content()
}

@Composable
fun <D : Device<ID>, ID : Device.Identifier> DevicesListComposable(
    paddingValues: PaddingValues,
    isToggleable: Boolean,
    deviceManagerState: DeviceManager.State,
    devices: FlowResult<DevicesState<D, ID>, Error>,
    onToggleManager: (Boolean) -> Unit,
    onDeviceSelected: (ID) -> Unit,
) {
    FlowResultComposable(
        flowResult = devices,
        paddingValues = paddingValues,
    ) { devicesState ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            if (devicesState.isSearching) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
            ) {
                item {
                    DeviceManagerStateLayout(
                        isToggleable = isToggleable,
                        deviceManagerState = deviceManagerState,
                        onToggleManager = onToggleManager,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                items(items = devicesState.devices) {
                    DeviceListItem(
                        device = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onDeviceSelected(it.identifier) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceManagerStateLayout(
    isToggleable: Boolean,
    deviceManagerState: DeviceManager.State,
    onToggleManager: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val value = when (deviceManagerState) {
        DeviceManager.State.UNAVAILABLE -> false
        DeviceManager.State.DISABLED -> false
        DeviceManager.State.DISABLING -> false
        DeviceManager.State.ENABLING -> true
        DeviceManager.State.ENABLED -> true
    }

    if (!isToggleable && value) {
        return
    }

    ElevatedCard(
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = colorScheme.primaryContainer,
        ),
    ) {
        Row(
            modifier = modifier
                .toggleable(
                    value = value,
                    enabled = isToggleable && when (deviceManagerState) {
                        DeviceManager.State.UNAVAILABLE -> false
                        DeviceManager.State.DISABLED -> true
                        DeviceManager.State.DISABLING -> false
                        DeviceManager.State.ENABLING -> false
                        DeviceManager.State.ENABLED -> true
                    },
                    role = Role.Switch,
                    onValueChange = { onToggleManager(it) },
                )
                .padding(4.dp, 24.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(
                    when (deviceManagerState) {
                        DeviceManager.State.UNAVAILABLE -> Res.string.device_manager_state_unavailable
                        DeviceManager.State.DISABLED -> Res.string.device_manager_state_disabled
                        DeviceManager.State.DISABLING -> Res.string.device_manager_state_disabling
                        DeviceManager.State.ENABLING -> Res.string.device_manager_state_enabling
                        DeviceManager.State.ENABLED -> Res.string.device_manager_state_enabled
                    }
                ),
                modifier = Modifier
                    .padding(start = 24.dp, end = 8.dp)
                    .weight(1f),
                style = MaterialTheme.typography.titleLarge,
            )
            Switch(
                checked = value,
                onCheckedChange = null,
                modifier = Modifier.padding(end = 16.dp),
            )
        }
    }
}

@Composable
private fun <D : Device<ID>, ID : Device.Identifier> DeviceListItem(
    device: D,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text = device.displayName ?: stringResource(Res.string.unknown_device),
            )
        },
        modifier = modifier,
        overlineContent = {
            when (device) {
                is BluetoothDevice -> Text(
                    text = stringResource(
                        when (device.state) {
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

                else -> {}
            }
        },
        supportingContent = {
            when (device) {
                is BluetoothDevice -> device.identifier.macAddress
                is BluetoothLeDevice -> device.identifier.toString()
                is DemoDevice -> null
                is NetworkDevice -> "${device.hostname}:${device.port}"
                is UsbDevice -> "0x${
                    device.vendorId.toHexString()
                }:0x${
                    device.productId.toHexString()
                }"
            }?.let {
                Text(
                    text = it,
                )
            }
        },
        leadingContent = {
            Icon(
                painter = painterResource(device.identifier.deviceType.drawableResource),
                contentDescription = stringResource(device.identifier.deviceType.stringResource),
            )
        },
    )
}
