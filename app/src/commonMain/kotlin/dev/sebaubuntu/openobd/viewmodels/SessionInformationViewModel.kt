/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import dev.sebaubuntu.openobd.models.SessionInformation
import dev.sebaubuntu.openobd.repositories.ObdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn

class SessionInformationViewModel(
    obdRepository: ObdRepository,
) : ViewModel() {
    /**
     * Get the [SessionInformation] related to the current session.
     */
    val sessionInformation = obdRepository.sessionStatus
        .flatMapLatestFlowResult {
            combine(
                obdRepository.pollCommand(
                    command = GetDeviceDescriptionCommand,
                    pollIntervalMs = null,
                ),
                obdRepository.pollCommand(
                    command = GetDeviceIdentifierCommand,
                    pollIntervalMs = null,
                ),
                obdRepository.pollCommand(
                    command = GetVersionIdCommand,
                    pollIntervalMs = null,
                ),
                obdRepository.pollCommand(
                    command = ReadInputVoltageCommand,
                    pollIntervalMs = SESSION_INFORMATION_POLL_INTERVAL_MS,
                ),
                obdRepository.pollCommand(
                    command = DescribeProtocolByNumberCommand,
                    pollIntervalMs = null,
                ),
                obdRepository.pollCommand(
                    command = DescribeProtocolCommand,
                    pollIntervalMs = null,
                ),
                obdRepository.pollCommand(
                    command = GetIgnMonCommand,
                    pollIntervalMs = SESSION_INFORMATION_POLL_INTERVAL_MS,
                ),
            ) { results ->
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
