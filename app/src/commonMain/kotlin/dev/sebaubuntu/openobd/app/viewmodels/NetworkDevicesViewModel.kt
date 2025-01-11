/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.app.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.app.repositories.NetworkRepository
import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch

class NetworkDevicesViewModel(
    private val networkRepository: NetworkRepository,
    deviceConnectionRepository: DeviceConnectionRepository,
) : BaseDevicesViewModel<NetworkDevice, NetworkDevice.Identifier>(
    networkRepository,
    deviceConnectionRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun get(
        identifier: NetworkDevice.Identifier,
    ) = networkRepository.get(identifier)
        .mapLatest { device ->
            device?.let {
                FlowResult.Success<_, Error>(it)
            } ?: FlowResult.Error(Error.NOT_FOUND)
        }

    fun createDevice(
        displayName: String,
        hostname: String,
        port: Int,
    ) = viewModelScope.launch(Dispatchers.IO) {
        networkRepository.create(displayName, hostname, port)
    }

    fun updateDevice(
        identifier: NetworkDevice.Identifier,
        displayName: String,
        hostname: String,
        port: Int,
    ) = viewModelScope.launch(Dispatchers.IO) {
        networkRepository.update(identifier, displayName, hostname, port)
    }

    fun deleteDevice(
        identifier: NetworkDevice.Identifier,
    ) = viewModelScope.launch(Dispatchers.IO) {
        networkRepository.delete(identifier)
    }
}
