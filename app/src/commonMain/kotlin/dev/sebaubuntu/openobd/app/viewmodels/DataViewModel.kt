/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.app.repositories.Elm327Repository
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.elm327.models.CanResponse
import dev.sebaubuntu.openobd.obd2.commands.GetDataCommand
import dev.sebaubuntu.openobd.obd2.models.DataType
import dev.sebaubuntu.openobd.obd2.models.SupportedParameterIds
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

/**
 * Base view model for [CurrentDataViewModel] and [FreezeFrameDataViewModel].
 */
abstract class DataViewModel(
    private val elm327Repository: Elm327Repository,
) : ViewModel() {
    protected abstract fun <T> dataCommandBuilder(dataType: DataType<T>): GetDataCommand<T>

    private val pidSupported0120Command by lazy {
        dataCommandBuilder(DataType.PID_SUPPORTED_01_20)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val supportedParameterIds = elm327Repository.pollCommand(
        command = pidSupported0120Command,
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
                            elm327Repository.executeCommand(
                                dataCommandBuilder(supportedParameterIdsCommand)
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
                combine(it) { dataToValue ->
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
        data: Result<CanResponse<SupportedParameterIds>, Error>,
    ) = data.getOrNull()?.let { obdResponse ->
        // We can poll the parameter ID if at least one control module supports it
        obdResponse.value.values.forEach {
            addAll(it)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> DataType<T>.asFlow() = elm327Repository.pollCommand(
        dataCommandBuilder(this),
        1000u,
    ).mapLatest {
        it.getOrNull()?.let { obdResponse ->
            this to obdResponse
        }
    }

    companion object {
        private val optionalSupportedParameterIdsCommands = listOf(
            DataType.PID_SUPPORTED_21_40,
            DataType.PID_SUPPORTED_41_60,
            DataType.PID_SUPPORTED_61_80,
            DataType.PID_SUPPORTED_81_A0,
            DataType.PID_SUPPORTED_A1_C0,
            DataType.PID_SUPPORTED_C1_E0,
        )

        private val allDataTypes by lazy {
            DataType.all.minus(optionalSupportedParameterIdsCommands.map { it.parameterId }.toSet())
        }
    }
}
