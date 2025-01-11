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
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.mapLatestDataOrNull
import dev.sebaubuntu.openobd.obd.commands.obd.ClearDiagnosticTroubleCodesCommand
import dev.sebaubuntu.openobd.obd.commands.obd.GetDiagnosticTroubleCodesCommand
import dev.sebaubuntu.openobd.obd.commands.obd.GetPendingDiagnosticTroubleCodesCommand
import dev.sebaubuntu.openobd.obd.commands.obd.GetPermanentDiagnosticTroubleCodesCommand
import dev.sebaubuntu.openobd.obd.commands.obd.GetStoredDiagnosticTroubleCodesCommand
import dev.sebaubuntu.openobd.obd.models.ControlModule
import dev.sebaubuntu.openobd.obd.models.DiagnosticTroubleCode
import dev.sebaubuntu.openobd.repositories.ObdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DiagnosticTroubleCodesViewModel(
    val obdRepository: ObdRepository,
) : ViewModel() {
    enum class CodeStatus {
        STORED,
        PENDING,
        PERMANENT,
    }

    data class CodeWithControlModules(
        val code: DiagnosticTroubleCode,
        val controlModulesWithStatus: List<ControlModuleWithStatus>,
    ) {
        data class ControlModuleWithStatus(
            val controlModule: ControlModule,
            val status: Set<CodeStatus>,
        )
    }

    val codesWithControlModules = combine(
        GetStoredDiagnosticTroubleCodesCommand.asFlowResult(),
        GetPendingDiagnosticTroubleCodesCommand.asFlowResult(),
        GetPermanentDiagnosticTroubleCodesCommand.asFlowResult(),
    ) { stored, pending, permanent ->
        val allDtcs = buildSet {
            addAll(stored.values.flatten())
            addAll(pending.values.flatten())
            addAll(permanent.values.flatten())
        }.toList().sorted()

        buildMap<DiagnosticTroubleCode, MutableMap<ControlModule, MutableSet<CodeStatus>>> {
            allDtcs.forEach { dtc ->
                mapOf(
                    stored to CodeStatus.STORED,
                    pending to CodeStatus.PENDING,
                    permanent to CodeStatus.PERMANENT,
                ).forEach { (dtcList, codeStatus) ->
                    dtcList.forEach { (controlModule, controlModuleCodes) ->
                        if (dtc in controlModuleCodes) {
                            getOrPut(dtc) { mutableMapOf() }
                                .getOrPut(controlModule) { mutableSetOf() }
                                .add(codeStatus)
                        }
                    }
                }
            }
        }.map { (dtc, controlModules) ->
            CodeWithControlModules(
                code = dtc,
                controlModulesWithStatus = controlModules.map { (controlModule, status) ->
                    CodeWithControlModules.ControlModuleWithStatus(
                        controlModule = controlModule,
                        status = status,
                    )
                },
            )
        }.takeIf { it.isNotEmpty() }?.let {
            FlowResult.Success<_, Error>(it)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun GetDiagnosticTroubleCodesCommand.asFlowResult() =
        obdRepository.pollCommand(this, 5000)
            .asFlowResult()
            .mapLatestDataOrNull()
            .mapLatest { it?.value.orEmpty() }
}
