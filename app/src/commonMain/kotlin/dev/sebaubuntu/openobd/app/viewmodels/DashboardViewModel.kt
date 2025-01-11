/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.app.models.Dashboard
import dev.sebaubuntu.openobd.app.repositories.Elm327Repository
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.core.models.value.Frequency.Companion.revolutionsPerMinute
import dev.sebaubuntu.openobd.core.models.value.Length.Companion.kilometers
import dev.sebaubuntu.openobd.core.models.value.Percentage.Companion.percent
import dev.sebaubuntu.openobd.core.models.value.Speed.Companion.kilometersPerHour
import dev.sebaubuntu.openobd.core.models.value.Temperature.Companion.celsius
import dev.sebaubuntu.openobd.elm327.models.CanResponse
import dev.sebaubuntu.openobd.obd2.commands.GetCurrentDataCommand
import dev.sebaubuntu.openobd.obd2.models.DataType
import dev.sebaubuntu.openobd.obd2.models.FuelType
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
 * Dashboard view model.
 */
class DashboardViewModel(
    private val elm327Repository: Elm327Repository,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val supportedParameterIds = elm327Repository.pollCommand(
        command = GetCurrentDataCommand(DataType.PID_SUPPORTED_01_20),
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
                                GetCurrentDataCommand(supportedParameterIdsCommand)
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

    private val ambientAirTemperature = DataType.AMBIENT_AIR_TEMPERATURE.asSharedFlow(
        pollIntervalMs = SLOW_DATA_POLL_INTERVAL_MS,
        defaultValue = 0.celsius,
    )
    private val engineCoolantTemperature = DataType.ENGINE_COOLANT_TEMPERATURE.asSharedFlow(
        pollIntervalMs = MEDIUM_DATA_POLL_INTERVAL_MS,
        defaultValue = 0.celsius,
    )
    private val engineOilTemperature = DataType.ENGINE_OIL_TEMPERATURE.asSharedFlow(
        pollIntervalMs = MEDIUM_DATA_POLL_INTERVAL_MS,
        defaultValue = 0.celsius,
    )
    private val engineSpeed = DataType.ENGINE_SPEED.asSharedFlow(
        pollIntervalMs = FAST_DATA_POLL_INTERVAL_MS,
        defaultValue = 0.revolutionsPerMinute,
    )
    private val fuelTankLevelInput = DataType.FUEL_TANK_LEVEL_INPUT.asSharedFlow(
        pollIntervalMs = MEDIUM_DATA_POLL_INTERVAL_MS,
        defaultValue = 0.percent,
    )
    private val fuelType = DataType.FUEL_TYPE.asSharedFlow(
        pollIntervalMs = SLOW_DATA_POLL_INTERVAL_MS,
        defaultValue = FuelType.GASOLINE,
    )
    private val intakeAirTemperature = DataType.INTAKE_AIR_TEMPERATURE.asSharedFlow(
        pollIntervalMs = SLOW_DATA_POLL_INTERVAL_MS,
        defaultValue = 0.celsius,
    )
    private val odometer = DataType.ODOMETER.asSharedFlow(
        pollIntervalMs = SLOW_DATA_POLL_INTERVAL_MS,
        defaultValue = 0.kilometers,
    )
    private val vehicleSpeed = DataType.VEHICLE_SPEED.asSharedFlow(
        pollIntervalMs = FAST_DATA_POLL_INTERVAL_MS,
        defaultValue = 0.kilometersPerHour,
    )

    val dashboard = combine(
        fuelTankLevelInput,
        engineCoolantTemperature,
        engineSpeed,
        vehicleSpeed,
        odometer,
    ) { fuelTankLevelInput, engineCoolantTemperature, engineSpeed, vehicleSpeed, odometer ->
        Dashboard.DEFAULT.copy(
            vehicleSpeed = vehicleSpeed,
            engineSpeed = engineSpeed,
            odometer = odometer,
            fuelLevel = fuelTankLevelInput,
            engineCoolantTemperature = engineCoolantTemperature,
        )
    }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Dashboard.DEFAULT,
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
    private fun <T> DataType<T>.asSharedFlow(
        pollIntervalMs: UInt,
        defaultValue: T,
    ) = supportedParameterIds
        .flatMapLatest { supportedParameterIds ->
            when (supportedParameterIds.contains(parameterId)) {
                true -> elm327Repository.pollCommand(
                    GetCurrentDataCommand(this),
                    pollIntervalMs,
                ).mapLatest {
                    it.getOrNull()?.value?.values?.first() ?: defaultValue
                }

                false -> flowOf(defaultValue)
            }
        }
        .flowOn(Dispatchers.IO)
        .shareIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )

    companion object {
        private const val FAST_DATA_POLL_INTERVAL_MS = 100u
        private const val MEDIUM_DATA_POLL_INTERVAL_MS = 500u
        private const val SLOW_DATA_POLL_INTERVAL_MS = 5000u

        private val optionalSupportedParameterIdsCommands = listOf(
            DataType.PID_SUPPORTED_21_40,
            DataType.PID_SUPPORTED_41_60,
            DataType.PID_SUPPORTED_61_80,
            DataType.PID_SUPPORTED_81_A0,
            DataType.PID_SUPPORTED_A1_C0,
            DataType.PID_SUPPORTED_C1_E0,
        )
    }
}
