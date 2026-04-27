/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import dev.sebaubuntu.openobd.app.models.ConnectionStatus
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.network.elm327.Elm327Manager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import org.koin.core.annotation.Single

@Single
class ConnectionStatusRepository(
    deviceConnectionRepository: DeviceConnectionRepository,
    elm327Repository: Elm327Repository,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : Repository(coroutineScope, coroutineDispatcher) {
    val connectionStatus = combine(
        deviceConnectionRepository.device,
        deviceConnectionRepository.connection,
        elm327Repository.status,
    ) { device, connection, status ->
        when (device) {
            is FlowResult.Loading -> ConnectionStatus.IDLE

            is FlowResult.Success -> when (connection) {
                FlowResult.Loading -> ConnectionStatus.CONNECTING

                is FlowResult.Success -> when (status) {
                    Elm327Manager.Status.IDLE -> ConnectionStatus.IDLE

                    Elm327Manager.Status.INITIALIZING -> ConnectionStatus.INITIALIZING

                    Elm327Manager.Status.READY -> ConnectionStatus.READY
                }

                is FlowResult.Failure -> ConnectionStatus.FAILED_CONNECTION
            }

            is FlowResult.Failure -> ConnectionStatus.IDLE
        }
    }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ConnectionStatus.IDLE,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val connectionStatusResult = connectionStatus
        .mapLatest { connectionStatus ->
            when (connectionStatus) {
                ConnectionStatus.IDLE -> FlowResult.Failure(Error.NOT_FOUND)
                ConnectionStatus.CONNECTING -> FlowResult.Loading
                ConnectionStatus.INITIALIZING -> FlowResult.Loading
                ConnectionStatus.READY -> FlowResult.Success(Unit)
                ConnectionStatus.FAILED_CONNECTION -> FlowResult.Failure(Error.IO)
                ConnectionStatus.FAILED_INITIALIZATION -> FlowResult.Failure(Error.IO)
            }
        }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading,
        )
}
