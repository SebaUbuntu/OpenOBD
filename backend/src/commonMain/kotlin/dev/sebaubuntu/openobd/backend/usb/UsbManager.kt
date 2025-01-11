/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.usb

import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.backend.models.Socket
import dev.sebaubuntu.openobd.backend.models.UsbDevice
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * USB manager.
 */
interface UsbManager : DeviceManager<UsbDevice, UsbDevice.Identifier> {
    /**
     * Get a flow of the list of the connected devices.
     */
    fun devices(): Flow<Result<List<UsbDevice>, Error>>

    companion object {
        val DEFAULT = object : UsbManager {
            override fun device(
                identifier: UsbDevice.Identifier,
            ) = flowOf(Result.Error<UsbDevice, _>(Error.NOT_IMPLEMENTED))

            override fun connection(
                identifier: UsbDevice.Identifier,
            ) = flowOf(Result.Error<Socket, _>(Error.NOT_IMPLEMENTED))

            override fun devices() = flowOf(
                Result.Error<List<UsbDevice>, _>(Error.NOT_IMPLEMENTED)
            )
        }
    }
}
