/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.usb

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.backend.models.DevicesState
import dev.sebaubuntu.openobd.backend.models.Socket
import dev.sebaubuntu.openobd.backend.models.UsbDevice
import dev.sebaubuntu.openobd.core.ext.broadcastReceiverFlow
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.flatMapLatestFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.mapLatestData
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.mapLatestFlowResult
import dev.sebaubuntu.openobd.core.models.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

class AndroidUsbManager(
    context: Context,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : UsbManager {
    private val usbManager = context.getSystemService(android.hardware.usb.UsbManager::class.java)

    private val deviceList = combine(
        context.broadcastReceiverFlow(
            IntentFilter(android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED),
            ContextCompat.RECEIVER_EXPORTED,
            false,
            LOG_TAG,
        ).onStart { emit(Intent()) },
        context.broadcastReceiverFlow(
            IntentFilter(android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED),
            ContextCompat.RECEIVER_EXPORTED,
            false,
            LOG_TAG,
        ).onStart { emit(Intent()) }
    ) { _, _ ->
        FlowResult.Success<_, Error>(usbManager.deviceList.toMap())
    }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    override val isToggleable = false

    override fun state() = flowOf(DeviceManager.State.ENABLED)

    override fun devices() = deviceList
        .mapLatestData { devices ->
            DevicesState(
                devices = devices.map { (_, usbDevice) ->
                    usbDevice.toModel()
                },
                isSearching = false,
            )
        }
        .asResult()

    override fun device(identifier: UsbDevice.Identifier) = deviceList
        .device(identifier)
        .mapLatestData { it.toModel() }
        .asResult()

    override fun connection(identifier: UsbDevice.Identifier) = deviceList
        .device(identifier)
        .flatMapLatestFlowResult { device ->
            callbackFlow {
                //usbManager.requestPermission(device, PendingIntent.getActivity())
                val usbDeviceConnection = usbManager.openDevice(device)

                send(FlowResult.Error<Socket, _>(Error.NOT_IMPLEMENTED))

                awaitClose {
                    usbDeviceConnection.close()
                }
            }
        }
        .asResult()

    override fun setState(state: Boolean) = Result.Error<Unit, Error>(Error.NOT_IMPLEMENTED)

    private fun android.hardware.usb.UsbDevice.toModel() = UsbDevice(
        identifier = UsbDevice.Identifier(id = deviceId),
        displayName = productName ?: deviceName,
        vendorId = vendorId.toUInt(),
        productId = productId.toUInt(),
    )

    private fun Flow<FlowResult<Map<String, android.hardware.usb.UsbDevice>, Error>>.device(
        identifier: UsbDevice.Identifier
    ) = mapLatestFlowResult { devices ->
        devices.values.firstOrNull { usbDevice ->
            usbDevice.deviceId == identifier.id
        }?.let {
            FlowResult.Success(it)
        } ?: FlowResult.Error(Error.NOT_FOUND)
    }

    companion object {
        private val LOG_TAG = AndroidUsbManager::class.simpleName!!
    }
}
