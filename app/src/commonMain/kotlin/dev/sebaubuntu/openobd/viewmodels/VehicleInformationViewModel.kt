/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.obd2.commands.GetVehicleInformationCommand
import dev.sebaubuntu.openobd.obd2.models.ObdResponse
import dev.sebaubuntu.openobd.obd2.models.SupportedParameterIds
import dev.sebaubuntu.openobd.obd2.models.VehicleInformationType
import dev.sebaubuntu.openobd.repositories.ObdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

class VehicleInformationViewModel(
    private val obdRepository: ObdRepository,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val supportedParameterIds = obdRepository.pollCommand(
        command = GetVehicleInformationCommand(VehicleInformationType.PID_SUPPORTED_01_20),
        pollIntervalMs = null,
    )
        .mapLatest {
            buildSet {
                // First add 01-20 results
                addAllParameterIds(it)

                // Then ask for the rest of the supported parameter IDs
                optionalSupportedParameterIdsCommands.forEach { supportedParameterIdsCommand ->
                    if (contains(supportedParameterIdsCommand.parameterId)) {
                        addAllParameterIds(
                            obdRepository.executeCommand(
                                GetVehicleInformationCommand(supportedParameterIdsCommand)
                            )
                        )
                    }
                }
            }
        }
        .flowOn(Dispatchers.IO)
        .shareIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val dataToValue = supportedParameterIds
        .flatMapLatest { supportedParameterIds ->
            allDataTypes.mapNotNull { (parameterId, dataType) ->
                dataType.takeIf { parameterId in supportedParameterIds }?.asFlow()
            }.takeIf { it.isNotEmpty() }?.let {
                combine<_, _>(it) { dataToValue ->
                    FlowResult.Success<_, Error>(dataToValue.filterNotNull())
                }
            } ?: flowOf(FlowResult.Error(Error.NOT_FOUND))
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    private fun MutableSet<UByte>.addAllParameterIds(
        data: Result<ObdResponse<SupportedParameterIds>, Error>,
    ) = data.getOrNull()?.let { obdResponse ->
        // We can poll the parameter ID if at least one control module supports it
        obdResponse.value.values.forEach {
            addAll(it)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> VehicleInformationType<T>.asFlow() = obdRepository.pollCommand(
        GetVehicleInformationCommand(this),
        1000,
    ).mapLatest {
        it.getOrNull()?.let { obdResponse ->
            this to obdResponse
        }
    }

    companion object {
        private val optionalSupportedParameterIdsCommands =
            listOf<VehicleInformationType<SupportedParameterIds>>()

        private val allDataTypes by lazy {
            VehicleInformationType.all.minus(
                optionalSupportedParameterIdsCommands.map { it.parameterId }.toSet()
            )
        }
    }
}
