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
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.backend.models.UsbDevice
import dev.sebaubuntu.openobd.ui.composables.FlowResultComposable
import dev.sebaubuntu.openobd.viewmodels.UsbDevicesViewModel
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.connection_type_usb
import openobd.app.generated.resources.ic_usb
import openobd.app.generated.resources.unknown_device
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun UsbDevicesScreen(
    paddingValues: PaddingValues,
) {
    val usbDevicesViewModel = koinViewModel<UsbDevicesViewModel>()

    val usbDevices = usbDevicesViewModel.devices.collectAsStateWithLifecycle()

    FlowResultComposable(
        flowResult = usbDevices.value,
        paddingValues = paddingValues,
    ) {
        UsbDevicesScreen(
            paddingValues = paddingValues,
            usbDevices = it,
            onDeviceSelected = usbDevicesViewModel::selectDevice,
        )
    }
}

@Composable
private fun UsbDevicesScreen(
    paddingValues: PaddingValues,
    usbDevices: List<UsbDevice>,
    onDeviceSelected: (UsbDevice.Identifier) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        items(items = usbDevices) {
            UsbDeviceListItem(
                usbDevice = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDeviceSelected(it.identifier) }
            )
        }
    }
}

@Composable
private fun UsbDeviceListItem(
    usbDevice: UsbDevice,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text = usbDevice.displayName ?: stringResource(Res.string.unknown_device),
            )
        },
        modifier = modifier,
        supportingContent = {
            Text(
                text = "0x${
                    usbDevice.vendorId.toHexString()
                }:0x${
                    usbDevice.productId.toHexString()
                }",
            )
        },
        leadingContent = {
            Icon(
                painter = painterResource(Res.drawable.ic_usb),
                contentDescription = stringResource(Res.string.connection_type_usb),
            )
        },
    )
}
