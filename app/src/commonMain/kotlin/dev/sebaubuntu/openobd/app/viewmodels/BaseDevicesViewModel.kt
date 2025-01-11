/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.app.repositories.BaseDeviceRepository
import dev.sebaubuntu.openobd.app.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.backend.models.Device
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.core.models.FlowResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class BaseDevicesViewModel<D : Device<ID>, ID : Device.Identifier>(
    private val deviceRepository: BaseDeviceRepository<*, D, ID>,
    private val deviceConnectionRepository: DeviceConnectionRepository,
) : ViewModel() {
    val isToggleable = deviceRepository.isToggleable

    val state = deviceRepository.state
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = DeviceManager.State.UNAVAILABLE,
        )

    val devices = deviceRepository.devices
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )

    fun setState(enabled: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        deviceRepository.setState(enabled)
    }

    fun selectDevice(identifier: ID) = viewModelScope.launch(Dispatchers.IO) {
        deviceConnectionRepository.setDeviceIdentifier(identifier)
    }
}
