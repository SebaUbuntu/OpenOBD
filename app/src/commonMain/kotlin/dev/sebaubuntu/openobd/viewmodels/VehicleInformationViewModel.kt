/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.obd2.commands.GetVehicleInformationCommand
import dev.sebaubuntu.openobd.obd2.models.VehicleInformationType
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
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
    deviceConnectionRepository: DeviceConnectionRepository,
    private val obdRepository: ObdRepository,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val supportedParameterIds = deviceConnectionRepository.device
        .mapLatest {
            buildSet {
                // Service 00 is always supported
                add(VehicleInformationType.PID_SUPPORTED_01_20.parameterId)

                supportedParameterIdsCommands.forEach { supportedParameterIdsCommand ->
                    if (contains(supportedParameterIdsCommand.parameterId)) {
                        obdRepository.executeCommand(
                            GetVehicleInformationCommand(supportedParameterIdsCommand)
                        ).getOrNull()?.let { supportedParameterIds ->
                            // We can poll the parameter ID if at least one control module
                            // supports it
                            supportedParameterIds.value.values.forEach {
                                addAll(it)
                            }
                        }
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
        private val supportedParameterIdsCommands = listOf(
            VehicleInformationType.PID_SUPPORTED_01_20,
        )

        private val allDataTypes by lazy {
            VehicleInformationType.all.minus(
                supportedParameterIdsCommands.map { it.parameterId }.toSet()
            )
        }
    }
}
