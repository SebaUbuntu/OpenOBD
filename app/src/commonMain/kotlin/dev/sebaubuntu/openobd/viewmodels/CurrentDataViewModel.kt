/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.obd.commands.obd.GetCurrentDataCommand
import dev.sebaubuntu.openobd.obd.models.Data
import dev.sebaubuntu.openobd.repositories.ObdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

class CurrentDataViewModel(
    private val obdRepository: ObdRepository,
) : ViewModel() {
    val dataToValue = combine(
        allSensors.map { it.asFlow() }
    ) { allSensors ->
        allSensors.filter { it.second != null }
    }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf(),
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun <T> Data<T>.asFlow() = obdRepository.pollCommand(
        GetCurrentDataCommand(this),
        1000,
    ).mapLatest {
        this to it.getOrNull()
    }

    companion object {
        private val allSensors = listOf(
            Data.CALCULATED_ENGINE_LOAD,
            Data.ENGINE_COOLANT_TEMPERATURE,
            Data.ENGINE_SPEED,
            Data.VEHICLE_SPEED,
            Data.INTAKE_AIR_TEMPERATURE,
            Data.THROTTLE_POSITION,
            Data.AMBIENT_AIR_TEMPERATURE,
            Data.FUEL_TYPE,
            Data.ENGINE_OIL_TEMPERATURE,
            Data.DPF_TEMPERATURE,
            Data.ODOMETER,
        )
    }
}
