/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import dev.sebaubuntu.openobd.app.repositories.DemoRepository
import dev.sebaubuntu.openobd.app.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.network.devices.models.DemoDevice

class DemoDevicesViewModel(
    demoRepository: DemoRepository,
    deviceConnectionRepository: DeviceConnectionRepository,
) : BaseDevicesViewModel<DemoDevice, DemoDevice.Identifier>(
    demoRepository,
    deviceConnectionRepository,
)
