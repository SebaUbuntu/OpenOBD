/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import dev.sebaubuntu.openobd.obd2.commands.GetFreezeFrameDataCommand
import dev.sebaubuntu.openobd.obd2.models.DataType
import dev.sebaubuntu.openobd.repositories.ObdRepository

/**
 * Freeze frame data view model.
 */
class FreezeFrameDataViewModel(
    obdRepository: ObdRepository,
) : DataViewModel(obdRepository) {
    override fun <T> dataCommandBuilder(dataType: DataType<T>) = GetFreezeFrameDataCommand(dataType)
}
