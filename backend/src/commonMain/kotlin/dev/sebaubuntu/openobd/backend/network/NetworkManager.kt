/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.network

import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.backend.models.Socket
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.flatMapLatestFlowResult
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.logging.Logger
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.asSink
import io.ktor.utils.io.asSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf

/**
 * Network manager.
 */
class NetworkManager : DeviceManager<NetworkDevice, NetworkDevice.Identifier> {
    // TODO
    val devices = listOf(
        NetworkDevice(
            NetworkDevice.Identifier(0),
            "ELM327-emulator",
            "127.0.0.1",
            35000,
        ),
    )

    override fun device(
        identifier: NetworkDevice.Identifier,
    ) = flowOf(
        devices.firstOrNull { it.identifier == identifier }?.let {
            Result.Success(it)
        } ?: Result.Error<NetworkDevice, _>(Error.NOT_FOUND)
    )

    override fun connection(
        identifier: NetworkDevice.Identifier,
    ) = device(identifier)
        .asFlowResult()
        .flatMapLatestFlowResult { device ->
            callbackFlow {
                val selectorManager = SelectorManager()

                val socket = runCatching {
                    aSocket(selectorManager)
                        .tcp()
                        .connect(device.hostname, device.port)
                }.onSuccess {
                    val receiveChannel = it.openReadChannel()
                    val sendChannel = it.openWriteChannel(autoFlush = true)

                    val modelSocket = Socket(
                        rawSource = receiveChannel.asSource(),
                        rawSink = sendChannel.asSink(),
                    )

                    send(FlowResult.Success<_, Error>(modelSocket))
                }.onFailure {
                    Logger.error(LOG_TAG, it) { "Failed to connect to network device" }
                    send(FlowResult.Error<Socket, _>(Error.IO))
                }.getOrNull()

                awaitClose {
                    socket?.close()
                    selectorManager.close()
                }
            }
        }
        .asResult()

    companion object {
        private val LOG_TAG = NetworkManager::class.simpleName!!
    }
}
