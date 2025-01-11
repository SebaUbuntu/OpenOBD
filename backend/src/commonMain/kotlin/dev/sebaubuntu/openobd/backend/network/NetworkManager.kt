/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.network

import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.backend.models.DevicesState
import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.backend.models.NetworkDevice.Companion.toModel
import dev.sebaubuntu.openobd.backend.models.Socket
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.storage.database.AppDatabase
import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.utils.io.asSink
import io.ktor.utils.io.asSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

/**
 * Network manager.
 */
class NetworkManager(
    appDatabase: AppDatabase,
) : DeviceManager<NetworkDevice, NetworkDevice.Identifier> {
    private val dao = appDatabase.networkDeviceDao()

    override val isToggleable = false

    override fun state() = flowOf(DeviceManager.State.ENABLED)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun devices() = dao.getAll().mapLatest { networkDevices ->
        Result.Success<_, Error>(
            DevicesState(
                devices = networkDevices.map { it.toModel() },
                isSearching = false,
            )
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun device(
        identifier: NetworkDevice.Identifier,
    ) = dao.getById(identifier.networkDeviceId).mapLatest { networkDevice ->
        networkDevice?.let {
            Result.Success<_, Error>(it.toModel())
        } ?: Result.Error(Error.NOT_FOUND)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun connection(
        identifier: NetworkDevice.Identifier
    ) = dao.getById(identifier.networkDeviceId)
        .distinctUntilChanged()
        .flatMapLatest { networkDevice ->
            networkDevice?.let {
                callbackFlow<Result<Socket, Error>> {
                    val selectorManager = SelectorManager()

                    val socket = runCatching {
                        aSocket(selectorManager)
                            .tcp()
                            .connect(it.hostname, it.port)
                    }.onSuccess { socket ->
                        val receiveChannel = socket.openReadChannel()
                        val sendChannel = socket.openWriteChannel(autoFlush = true)

                        val modelSocket = Socket(
                            rawSource = receiveChannel.asSource(),
                            rawSink = sendChannel.asSink(),
                        )

                        send(Result.Success(modelSocket))
                    }.onFailure { throwable ->
                        Logger.error(LOG_TAG, throwable) { "Failed to connect to network device" }
                        send(Result.Error(Error.IO))
                    }.getOrNull()

                    awaitClose {
                        socket?.close()
                        selectorManager.close()
                    }
                }
            } ?: flowOf(Result.Error(Error.NOT_FOUND))
        }

    override fun setState(state: Boolean) = Result.Error<Unit, _>(Error.NOT_IMPLEMENTED)

    companion object {
        private val LOG_TAG = NetworkManager::class.simpleName!!
    }
}
