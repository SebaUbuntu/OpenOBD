/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.network

import dev.sebaubuntu.openobd.backend.ext.toModel
import dev.sebaubuntu.openobd.backend.models.ConnectionStatus
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.flatMapLatestData
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.foldLatest
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Socket
import dev.sebaubuntu.openobd.logging.Logger
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

/**
 * Network manager.
 */
class NetworkManager(
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : DeviceManager<NetworkDevice, NetworkDevice.Identifier> {
    private val selectorManager = SelectorManager(coroutineDispatcher)

    // TODO
    val devices = mutableMapOf<Int, NetworkDevice>().apply {
        this[0] = NetworkDevice(
            NetworkDevice.Identifier(0),
            "ELM327-emulator",
            "127.0.0.1",
            35000,
        )
    }

    override fun device(
        identifier: NetworkDevice.Identifier,
    ) = flowOf(
        devices[identifier.networkDeviceId]?.let {
            Result.Success(it)
        } ?: Result.Error<NetworkDevice, _>(Error.NOT_FOUND)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun connection(
        identifier: NetworkDevice.Identifier,
    ) = device(identifier)
        .asFlowResult()
        .flatMapLatestData { device ->
            callbackFlow {
                runCatching {
                    val socket = aSocket(selectorManager)
                        .tcp()
                        .connect(device.hostname, device.port)

                    val receiveChannel = socket.openReadChannel()
                    val sendChannel = socket.openWriteChannel(autoFlush = true)

                    val modelSocket = object : Socket {
                        override val inputStream = receiveChannel.toModel()
                        override val outputStream = sendChannel.toModel()
                    }

                    send(
                        ConnectionStatus.Connected(
                            device = device,
                            socket = modelSocket,
                        )
                    )

                    awaitClose {
                        socket.close()
                    }
                }.onFailure {
                    Logger.error(LOG_TAG, it) { "Failed to connect to network device" }
                    send(ConnectionStatus.Lost(device))
                }
            }.mapLatest { FlowResult.Success(it) }
        }
        .foldLatest(
            onSuccess = { it },
            onError = { _, _ -> ConnectionStatus.Lost(null) }
        )

    companion object {
        private val LOG_TAG = NetworkManager::class.simpleName!!
    }
}
