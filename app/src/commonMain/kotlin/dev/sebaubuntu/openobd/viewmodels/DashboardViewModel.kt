/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.models.Dashboard
import dev.sebaubuntu.openobd.obd.commands.obd.GetCurrentDataCommand
import dev.sebaubuntu.openobd.obd.models.Data
import dev.sebaubuntu.openobd.repositories.ObdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

/**
 * Dashboard view model.
 */
class DashboardViewModel(
    private val obdRepository: ObdRepository,
) : ViewModel() {
    private val engineCoolantTemperature = Data.ENGINE_COOLANT_TEMPERATURE.asStateFlow(1000)
    private val engineSpeed = Data.ENGINE_SPEED.asStateFlow(200)
    private val vehicleSpeed = Data.VEHICLE_SPEED.asStateFlow(200)
    private val intakeAirTemperature = Data.INTAKE_AIR_TEMPERATURE.asStateFlow(5000)
    private val ambientAirTemperature = Data.AMBIENT_AIR_TEMPERATURE.asStateFlow(5000)
    private val fuelType = Data.FUEL_TYPE.asStateFlow(5000)
    private val engineOilTemperature = Data.ENGINE_OIL_TEMPERATURE.asStateFlow(5000)
    private val odometer = Data.ODOMETER.asStateFlow(5000)

    val dashboard = combine(
        engineCoolantTemperature,
        engineSpeed,
        vehicleSpeed,
        fuelType,
        engineOilTemperature,
    ) { engineCoolantTemperature, engineSpeed, vehicleSpeed, fuelType, engineOilTemperature ->
        Dashboard.DEFAULT.copy(
            speedKph = vehicleSpeed.getOrNull()?.value?.toInt() ?: Dashboard.DEFAULT.speedKph,
            rpm = engineSpeed.getOrNull()?.value?.toInt() ?: Dashboard.DEFAULT.rpm,
            coolantTemperatureCelsius = engineCoolantTemperature.getOrNull()?.value?.toInt()
                ?: Dashboard.DEFAULT.coolantTemperatureCelsius,
        )
    }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Dashboard.DEFAULT,
        )

    private fun <T> Data<T>.asStateFlow(pollIntervalMs: Long) = obdRepository.pollCommand(
        GetCurrentDataCommand(this),
        pollIntervalMs,
    )
        .flowOn(Dispatchers.IO)
        .shareIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            replay = 1,
        )
}
