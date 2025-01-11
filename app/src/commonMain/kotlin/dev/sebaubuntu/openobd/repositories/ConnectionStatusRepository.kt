/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.models.ConnectionStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

class ConnectionStatusRepository(
    deviceConnectionRepository: DeviceConnectionRepository,
    obdRepository: ObdRepository,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : Repository(coroutineScope, coroutineDispatcher) {
    val connectionStatus = combine(
        deviceConnectionRepository.device,
        deviceConnectionRepository.connection,
        obdRepository.sessionStatus,
    ) { device, connection, sessionStatus ->
        when (device) {
            is FlowResult.Loading -> ConnectionStatus.IDLE

            is FlowResult.Success -> when (connection) {
                is FlowResult.Loading -> ConnectionStatus.CONNECTING

                is FlowResult.Success -> when (sessionStatus) {
                    is FlowResult.Loading -> ConnectionStatus.INITIALIZING

                    is FlowResult.Success -> ConnectionStatus.READY

                    is FlowResult.Error -> ConnectionStatus.FAILED_INITIALIZATION
                }

                is FlowResult.Error -> ConnectionStatus.FAILED_CONNECTION
            }

            is FlowResult.Error -> ConnectionStatus.IDLE
        }
    }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = ConnectionStatus.IDLE,
        )
}
