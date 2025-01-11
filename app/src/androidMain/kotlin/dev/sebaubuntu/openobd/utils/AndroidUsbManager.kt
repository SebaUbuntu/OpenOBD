/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.utils

import android.content.Context
import dev.sebaubuntu.openobd.backend.models.UsbDevice
import dev.sebaubuntu.openobd.backend.usb.UsbManager
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.flatMapLatestFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.mapLatestData
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.mapLatestFlowResult
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Socket
import dev.sebaubuntu.openobd.logging.Logger
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlin.coroutines.CoroutineContext

class AndroidUsbManager(
    context: Context,
    coroutineContext: CoroutineContext,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : UsbManager {
    private val usbManager = context.getSystemService(android.hardware.usb.UsbManager::class.java)

    private val devices = callbackFlow {
        val accessories = usbManager.deviceList

        send(FlowResult.Success<_, Error>(accessories))
    }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = CoroutineScope(coroutineContext),
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    override fun devices() = devices
        .mapLatestData { devices ->
            devices.values.map { usbDevice ->
                usbDevice.toModel()
            }
        }
        .asResult()

    override fun device(identifier: UsbDevice.Identifier) = devices
        .mapLatestFlowResult { devices ->
            devices.values.firstOrNull { usbDevice ->
                usbDevice.deviceId == identifier.id
            }?.let {
                FlowResult.Success(it.toModel())
            } ?: FlowResult.Error(Error.NOT_FOUND)
        }
        .asResult()

    override fun connection(identifier: UsbDevice.Identifier) = device(identifier)
        .asFlowResult()
        .flatMapLatestFlowResult { device ->
            callbackFlow {
                usbManager.openDevice(device)

                awaitClose {

                }
            }
        }
        .asResult()

    private fun android.hardware.usb.UsbDevice.toModel() = UsbDevice(
        identifier = UsbDevice.Identifier(id = deviceId),
        displayName = productName ?: deviceName,
        vendorId = vendorId.toUInt(),
        productId = productId.toUInt(),
    )

    companion object {
        private val LOG_TAG = AndroidUsbManager::class.simpleName!!
    }
}
