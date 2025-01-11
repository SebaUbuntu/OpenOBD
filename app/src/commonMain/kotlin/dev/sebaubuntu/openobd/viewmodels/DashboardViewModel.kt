/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.core.models.value.Frequency
import dev.sebaubuntu.openobd.core.models.value.Length
import dev.sebaubuntu.openobd.core.models.value.Percentage
import dev.sebaubuntu.openobd.core.models.value.Speed
import dev.sebaubuntu.openobd.core.models.value.Temperature
import dev.sebaubuntu.openobd.core.models.value.Value.Companion.asValue
import dev.sebaubuntu.openobd.models.Dashboard
import dev.sebaubuntu.openobd.obd2.commands.GetCurrentDataCommand
import dev.sebaubuntu.openobd.obd2.models.DataType
import dev.sebaubuntu.openobd.obd2.models.FuelType
import dev.sebaubuntu.openobd.obd2.models.ObdResponse
import dev.sebaubuntu.openobd.obd2.models.SupportedParameterIds
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

/**
 * Dashboard view model.
 */
class DashboardViewModel(
    private val obdRepository: ObdRepository,
) : ViewModel() {
    @OptIn(ExperimentalCoroutinesApi::class)
    val supportedParameterIds = obdRepository.pollCommand(
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
                            obdRepository.executeCommand(
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

    private val ambientAirTemperature = DataType.AMBIENT_AIR_TEMPERATURE.asStateFlow(
        pollIntervalMs = SLOW_DATA_POLL_INTERVAL_MS,
        defaultValue = 0f.asValue(Temperature.Unit.CELSIUS),
    )
    private val engineCoolantTemperature = DataType.ENGINE_COOLANT_TEMPERATURE.asStateFlow(
        pollIntervalMs = MEDIUM_DATA_POLL_INTERVAL_MS,
        defaultValue = 0f.asValue(Temperature.Unit.CELSIUS),
    )
    private val engineOilTemperature = DataType.ENGINE_OIL_TEMPERATURE.asStateFlow(
        pollIntervalMs = MEDIUM_DATA_POLL_INTERVAL_MS,
        defaultValue = 0f.asValue(Temperature.Unit.CELSIUS),
    )
    private val engineSpeed = DataType.ENGINE_SPEED.asStateFlow(
        pollIntervalMs = FAST_DATA_POLL_INTERVAL_MS,
        defaultValue = 0f.asValue(Frequency.Unit.REVOLUTION_PER_MINUTE),
    )
    private val fuelTankLevelInput = DataType.FUEL_TANK_LEVEL_INPUT.asStateFlow(
        pollIntervalMs = MEDIUM_DATA_POLL_INTERVAL_MS,
        defaultValue = 0f.asValue(Percentage.Unit.PERCENT),
    )
    private val fuelType = DataType.FUEL_TYPE.asStateFlow(
        pollIntervalMs = SLOW_DATA_POLL_INTERVAL_MS,
        defaultValue = FuelType.GASOLINE,
    )
    private val intakeAirTemperature = DataType.INTAKE_AIR_TEMPERATURE.asStateFlow(
        pollIntervalMs = SLOW_DATA_POLL_INTERVAL_MS,
        defaultValue = 0f.asValue(Temperature.Unit.CELSIUS),
    )
    private val odometer = DataType.ODOMETER.asStateFlow(
        pollIntervalMs = SLOW_DATA_POLL_INTERVAL_MS,
        defaultValue = 0f.asValue(Length.Unit.KILOMETER),
    )
    private val vehicleSpeed = DataType.VEHICLE_SPEED.asStateFlow(
        pollIntervalMs = FAST_DATA_POLL_INTERVAL_MS,
        defaultValue = 0f.asValue(Speed.Unit.KILOMETER_PER_HOUR),
    )

    val dashboard = combine(
        fuelTankLevelInput,
        engineCoolantTemperature,
        engineSpeed,
        vehicleSpeed,
        odometer,
    ) { fuelTankLevelInput, engineCoolantTemperature, engineSpeed, vehicleSpeed, odometer ->
        Dashboard.DEFAULT.copy(
            speedKph = vehicleSpeed.value.toInt(),
            rpm = engineSpeed.value.toInt(),
            odometer = odometer.value.toInt(),
            fuelLevel = fuelTankLevelInput.value,
            coolantTemperatureCelsius = engineCoolantTemperature.value.toInt(),
        )
    }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Dashboard.DEFAULT,
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
    private fun <T> DataType<T>.asStateFlow(
        pollIntervalMs: Long,
        defaultValue: T,
    ) = supportedParameterIds
        .flatMapLatest { supportedParameterIds ->
            when (supportedParameterIds.contains(parameterId)) {
                true -> obdRepository.pollCommand(
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
        private const val FAST_DATA_POLL_INTERVAL_MS = 100L
        private const val MEDIUM_DATA_POLL_INTERVAL_MS = 500L
        private const val SLOW_DATA_POLL_INTERVAL_MS = 5000L

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
