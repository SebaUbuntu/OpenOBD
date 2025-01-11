/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.obd2.models.DataType

/**
 * Get data command.
 *
 * @param dataType The sensor to get the data from
 */
@OptIn(ExperimentalUnsignedTypes::class)
abstract class GetDataCommand<T>(
    serviceCode: UByte,
    private val dataType: DataType<T>,
) : ObdCommand<T>(serviceCode) {
    override val pid = ubyteArrayOf(dataType.parameterId)

    override val expectedDataBytes = dataType.expectedBytes

    override fun parseResponse(
        response: UByteArray,
    ) = Result.Success<T, Error>(dataType.responseParser(response))
}
