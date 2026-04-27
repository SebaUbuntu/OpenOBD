/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import dev.sebaubuntu.openobd.app.repositories.Elm327Repository
import dev.sebaubuntu.openobd.network.obd2.commands.GetCurrentDataCommand
import dev.sebaubuntu.openobd.network.obd2.models.DataType
import org.koin.core.annotation.KoinViewModel

/**
 * Current data view model.
 */
@KoinViewModel
class CurrentDataViewModel(
    elm327Repository: Elm327Repository,
) : DataViewModel(elm327Repository) {
    override fun <T> dataCommandBuilder(dataType: DataType<T>) = GetCurrentDataCommand(dataType)
}
