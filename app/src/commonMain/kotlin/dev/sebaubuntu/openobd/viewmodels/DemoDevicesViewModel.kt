/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import dev.sebaubuntu.openobd.backend.models.DemoDevice
import dev.sebaubuntu.openobd.repositories.DemoRepository
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository

class DemoDevicesViewModel(
    demoRepository: DemoRepository,
    deviceConnectionRepository: DeviceConnectionRepository,
) : BaseDevicesViewModel<DemoDevice, DemoDevice.Identifier>(
    demoRepository,
    deviceConnectionRepository,
)
