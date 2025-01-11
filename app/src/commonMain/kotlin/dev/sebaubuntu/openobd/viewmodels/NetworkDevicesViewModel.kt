/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.repositories.NetworkRepository

class NetworkDevicesViewModel(
    networkRepository: NetworkRepository,
    deviceConnectionRepository: DeviceConnectionRepository,
) : BaseDevicesViewModel<NetworkDevice, NetworkDevice.Identifier>(
    networkRepository,
    deviceConnectionRepository,
)
