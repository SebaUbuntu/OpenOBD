/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.app.repositories.Elm327Repository
import dev.sebaubuntu.openobd.app.repositories.ProfilesRepository
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.mapLatestDataOrNull
import dev.sebaubuntu.openobd.elm327.models.ControlModule
import dev.sebaubuntu.openobd.obd2.commands.ClearDiagnosticTroubleCodesCommand
import dev.sebaubuntu.openobd.obd2.commands.GetDiagnosticTroubleCodesCommand
import dev.sebaubuntu.openobd.obd2.commands.GetPendingDiagnosticTroubleCodesCommand
import dev.sebaubuntu.openobd.obd2.commands.GetPermanentDiagnosticTroubleCodesCommand
import dev.sebaubuntu.openobd.obd2.commands.GetStoredDiagnosticTroubleCodesCommand
import dev.sebaubuntu.openobd.obd2.models.DiagnosticTroubleCode
import dev.sebaubuntu.openobd.profiles.models.DtcInformation
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
    private val elm327Repository: Elm327Repository,
    profilesRepository: ProfilesRepository,
) : ViewModel() {
    enum class CodeStatus {
        STORED,
        PENDING,
        PERMANENT,
    }

    data class CodeWithControlModules(
        val code: DiagnosticTroubleCode,
        val information: DtcInformation?,
        val controlModulesWithStatus: List<ControlModuleWithStatus>,
    ) {
        data class ControlModuleWithStatus(
            val controlModule: ControlModule,
            val status: Set<CodeStatus>,
        )
    }

    val codesWithControlModules = combine(
        GetStoredDiagnosticTroubleCodesCommand.asFlow(),
        GetPendingDiagnosticTroubleCodesCommand.asFlow(),
        GetPermanentDiagnosticTroubleCodesCommand.asFlow(),
        profilesRepository.diagnosticTroubleCodes.mapLatestDataOrNull(),
    ) { stored, pending, permanent, diagnosticTroubleCodesDescription ->
        val allDtcs = buildSet {
            addAll(stored.values.flatten())
            addAll(pending.values.flatten())
            addAll(permanent.values.flatten())
        }.toList().sorted()

        buildMap<DiagnosticTroubleCode, MutableMap<ControlModule, MutableSet<CodeStatus>>> {
            allDtcs.forEach { dtc ->
                mapOf(
                    CodeStatus.STORED to stored,
                    CodeStatus.PENDING to pending,
                    CodeStatus.PERMANENT to permanent,
                ).forEach { (codeStatus, dtcList) ->
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
                information = diagnosticTroubleCodesDescription?.get(dtc),
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
        elm327Repository.executeCommand(ClearDiagnosticTroubleCodesCommand)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun GetDiagnosticTroubleCodesCommand.asFlow() =
        elm327Repository.pollCommand(this, 5000u)
            .asFlowResult()
            .mapLatestDataOrNull()
            .mapLatest { it?.value.orEmpty() }
}
