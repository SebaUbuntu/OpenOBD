/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.backend.models.Device
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

abstract class BaseDevicesViewModel<ID : Device.Identifier>(
    private val deviceConnectionRepository: DeviceConnectionRepository,
) : ViewModel() {
    fun selectDevice(identifier: ID) = viewModelScope.launch(Dispatchers.IO) {
        deviceConnectionRepository.setDeviceIdentifier(identifier)
    }
}
