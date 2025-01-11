/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.mapLatestData
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.mapLatestFlowResult
import dev.sebaubuntu.openobd.obd.commands.obd.ClearDiagnosticTroubleCodesCommand
import dev.sebaubuntu.openobd.obd.commands.obd.GetDiagnosticTroubleCodesCommand
import dev.sebaubuntu.openobd.obd.models.ControlModule
import dev.sebaubuntu.openobd.obd.models.DiagnosticTroubleCode
import dev.sebaubuntu.openobd.repositories.ObdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DiagnosticTroubleCodesViewModel(
    val obdRepository: ObdRepository,
) : ViewModel() {
    data class CodeWithControlModules(
        val code: DiagnosticTroubleCode,
        val controlModules: List<ControlModule>,
    )

    val codesWithControlModules = obdRepository.pollCommand(
        GetDiagnosticTroubleCodesCommand,
        5000,
    )
        .asFlowResult()
        .mapLatestFlowResult { obdResponse ->
            buildMap<DiagnosticTroubleCode, MutableList<ControlModule>> {
                obdResponse.value.forEach { (controlModule, codes) ->
                    codes.forEach { code ->
                        getOrPut(code) { mutableListOf() }.add(controlModule)
                    }
                }
            }.map { (code, controlModules) ->
                CodeWithControlModules(
                    code = code,
                    controlModules = controlModules,
                )
            }.takeIf { it.isNotEmpty() }?.let {
                FlowResult.Success(it)
            } ?: FlowResult.Error(Error.NOT_FOUND)
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    fun clearCodes() = viewModelScope.launch {
        obdRepository.executeCommand(ClearDiagnosticTroubleCodesCommand)
    }
}
