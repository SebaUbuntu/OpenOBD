/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.models.Dashboard
import dev.sebaubuntu.openobd.obd2.commands.GetCurrentDataCommand
import dev.sebaubuntu.openobd.obd2.models.DataType
import dev.sebaubuntu.openobd.repositories.ObdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
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
    private val ambientAirTemperature = DataType.AMBIENT_AIR_TEMPERATURE.asStateFlow(5000)
    private val engineCoolantTemperature = DataType.ENGINE_COOLANT_TEMPERATURE.asStateFlow(1000)
    private val engineOilTemperature = DataType.ENGINE_OIL_TEMPERATURE.asStateFlow(5000)
    private val engineSpeed = DataType.ENGINE_SPEED.asStateFlow(200)
    private val fuelTankLevelInput = DataType.FUEL_TANK_LEVEL_INPUT.asStateFlow(1000)
    private val fuelType = DataType.FUEL_TYPE.asStateFlow(5000)
    private val intakeAirTemperature = DataType.INTAKE_AIR_TEMPERATURE.asStateFlow(5000)
    private val odometer = DataType.ODOMETER.asStateFlow(5000)
    private val vehicleSpeed = DataType.VEHICLE_SPEED.asStateFlow(200)

    val dashboard = combine(
        fuelTankLevelInput,
        engineCoolantTemperature,
        engineSpeed,
        vehicleSpeed,
        odometer,
    ) { fuelTankLevelInput, engineCoolantTemperature, engineSpeed, vehicleSpeed, odometer ->
        Dashboard.DEFAULT.copy(
            speedKph = vehicleSpeed?.value?.toInt() ?: Dashboard.DEFAULT.speedKph,
            rpm = engineSpeed?.value?.toInt() ?: Dashboard.DEFAULT.rpm,
            odometer = odometer?.value?.toInt() ?: Dashboard.DEFAULT.odometer,
            fuelLevel = fuelTankLevelInput?.value ?: Dashboard.DEFAULT.fuelLevel,
            coolantTemperatureCelsius = engineCoolantTemperature?.value?.toInt()
                ?: Dashboard.DEFAULT.coolantTemperatureCelsius,
        )
    }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Dashboard.DEFAULT,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> DataType<T>.asStateFlow(pollIntervalMs: Long) = obdRepository.pollCommand(
        GetCurrentDataCommand(this),
        pollIntervalMs,
    )
        .mapLatest {
            it.getOrNull()?.value?.values?.first()
        }
        .flowOn(Dispatchers.IO)
        .shareIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )
}
