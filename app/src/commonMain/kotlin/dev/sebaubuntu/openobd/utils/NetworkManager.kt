/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.utils

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Socket
import dev.sebaubuntu.openobd.ext.toModel
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.models.DeviceConnection
import dev.sebaubuntu.openobd.models.NetworkDevice
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

/**
 * Network manager.
 */
class NetworkManager(
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    fun connect(networkDevice: NetworkDevice) = callbackFlow<Result<DeviceConnection, Error>> {
        val selectorManager = SelectorManager(coroutineDispatcher)

        val socket = runCatching {
            aSocket(selectorManager)
                .tcp()
                .connect(networkDevice.hostname, networkDevice.port)
        }.getOrElse {
            Logger.error(LOG_TAG, it) { "Failed to connect to network device" }
            null
        }

        socket?.let {
            val receiveChannel = it.openReadChannel()
            val sendChannel = it.openWriteChannel(autoFlush = true)

            val modelSocket = object : Socket {
                override val inputStream = receiveChannel.toModel()
                override val outputStream = sendChannel.toModel()
            }

            DeviceConnection(
                device = networkDevice,
                socket = modelSocket,
            )
        }?.also {
            trySend(Result.Success(it))
        } ?: run {
            trySend(Result.Error(Error.IO))
        }

        awaitClose {
            socket?.close()
            selectorManager.close()
        }
    }

    companion object {
        private val LOG_TAG = NetworkManager::class.simpleName!!
    }
}
