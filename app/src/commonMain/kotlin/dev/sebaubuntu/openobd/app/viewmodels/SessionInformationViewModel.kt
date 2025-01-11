/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.app.models.SessionInformation
import dev.sebaubuntu.openobd.app.repositories.ConnectionStatusRepository
import dev.sebaubuntu.openobd.app.repositories.Elm327Repository
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.flatMapLatestFlowResult
import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.core.models.value.Voltage
import dev.sebaubuntu.openobd.elm327.commands.DescribeProtocolByNumberCommand
import dev.sebaubuntu.openobd.elm327.commands.DescribeProtocolCommand
import dev.sebaubuntu.openobd.elm327.commands.GetDeviceDescriptionCommand
import dev.sebaubuntu.openobd.elm327.commands.GetDeviceIdentifierCommand
import dev.sebaubuntu.openobd.elm327.commands.GetIgnMonCommand
import dev.sebaubuntu.openobd.elm327.commands.GetVersionIdCommand
import dev.sebaubuntu.openobd.elm327.commands.ReadInputVoltageCommand
import dev.sebaubuntu.openobd.elm327.models.ObdProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

class SessionInformationViewModel(
    connectionStatusRepository: ConnectionStatusRepository,
    elm327Repository: Elm327Repository,
) : ViewModel() {
    /**
     * Get the [SessionInformation] related to the current session.
     */
    val sessionInformation = connectionStatusRepository.connectionStatusResult
        .flatMapLatestFlowResult {
            combine(
                elm327Repository.pollCommand(
                    command = GetDeviceDescriptionCommand,
                    pollIntervalMs = null,
                ),
                elm327Repository.pollCommand(
                    command = GetDeviceIdentifierCommand,
                    pollIntervalMs = null,
                ),
                elm327Repository.pollCommand(
                    command = GetVersionIdCommand,
                    pollIntervalMs = null,
                ),
                elm327Repository.pollCommand(
                    command = ReadInputVoltageCommand,
                    pollIntervalMs = SESSION_INFORMATION_POLL_INTERVAL_MS,
                ),
                elm327Repository.pollCommand(
                    command = DescribeProtocolByNumberCommand,
                    pollIntervalMs = null,
                ),
                elm327Repository.pollCommand(
                    command = DescribeProtocolCommand,
                    pollIntervalMs = null,
                ),
                elm327Repository.pollCommand(
                    command = GetIgnMonCommand,
                    pollIntervalMs = SESSION_INFORMATION_POLL_INTERVAL_MS,
                ),
            ) { results ->
                @Suppress("UNCHECKED_CAST")
                SessionInformation(
                    deviceDescription = results[0].getOrNull() as? String,
                    deviceIdentifier = results[1].getOrNull() as? String,
                    versionId = results[2].getOrNull() as? String,
                    inputVoltage = results[3].getOrNull() as? Voltage,
                    obdProtocol = results[4].getOrNull() as? Pair<Boolean, ObdProtocol>,
                    obdProtocolDescription = results[5].getOrNull() as? String,
                    ignition = results[6].getOrNull() as? Boolean,
                ).let { sessionInformation ->
                    FlowResult.Success(sessionInformation)
                }
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    companion object {
        private const val SESSION_INFORMATION_POLL_INTERVAL_MS = 1000u
    }
}
