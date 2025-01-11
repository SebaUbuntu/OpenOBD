/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.backend.models.UsbDevice
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.repositories.UsbRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class UsbDevicesViewModel(
    usbRepository: UsbRepository,
    deviceConnectionRepository: DeviceConnectionRepository,
) : BaseDevicesViewModel<UsbDevice.Identifier>(deviceConnectionRepository) {
    val devices = usbRepository.devices
        .asFlowResult()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = FlowResult.Loading(),
        )
}
