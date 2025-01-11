/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.models.ConnectionStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

class ConnectionStatusRepository(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    deviceConnectionRepository: DeviceConnectionRepository,
    obdRepository: ObdRepository,
) {
    val connectionStatus = combine(
        deviceConnectionRepository.device,
        deviceConnectionRepository.connection,
        obdRepository.sessionInfo,
    ) { device, connection, sessionInfo ->
        when (device) {
            is FlowResult.Loading -> ConnectionStatus.IDLE

            is FlowResult.Success -> when (connection) {
                is FlowResult.Loading -> ConnectionStatus.CONNECTING

                is FlowResult.Success -> when (sessionInfo) {
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
