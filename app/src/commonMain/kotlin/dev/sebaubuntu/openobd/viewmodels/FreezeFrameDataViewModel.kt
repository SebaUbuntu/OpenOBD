/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import dev.sebaubuntu.openobd.obd.commands.obd.GetFreezeFrameDataCommand
import dev.sebaubuntu.openobd.obd.models.DataType
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.repositories.ObdRepository

/**
 * Freeze frame data view model.
 */
class FreezeFrameDataViewModel(
    deviceConnectionRepository: DeviceConnectionRepository,
    obdRepository: ObdRepository,
) : DataViewModel(deviceConnectionRepository, obdRepository) {
    override fun <T> dataCommandBuilder(dataType: DataType<T>) = GetFreezeFrameDataCommand(dataType)
}
