/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import dev.sebaubuntu.openobd.obd2.commands.GetCurrentDataCommand
import dev.sebaubuntu.openobd.obd2.models.DataType
import dev.sebaubuntu.openobd.repositories.ObdRepository

/**
 * Current data view model.
 */
class CurrentDataViewModel(
    obdRepository: ObdRepository,
) : DataViewModel(obdRepository) {
    override fun <T> dataCommandBuilder(dataType: DataType<T>) = GetCurrentDataCommand(dataType)
}
