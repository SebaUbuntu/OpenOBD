/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.network

import dev.sebaubuntu.openobd.core.models.Socket
import dev.sebaubuntu.openobd.backend.ext.toModel
import dev.sebaubuntu.openobd.backend.models.ConnectionStatus
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.logging.Logger
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
) : DeviceManager<NetworkDevice, NetworkDevice.Identifier> {
    override fun connection(
        deviceIdentifier: NetworkDevice.Identifier,
    ) = callbackFlow<ConnectionStatus<NetworkDevice, NetworkDevice.Identifier>> {
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

            ConnectionStatus.Connected(
                device = networkDevice,
                socket = modelSocket,
            )
        }.also {
            send(it ?: ConnectionStatus.Lost(null))
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
