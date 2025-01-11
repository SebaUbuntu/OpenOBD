/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.obd

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.obd.models.Data

/**
 * Get data command.
 *
 * @param data The sensor to get the data from
 */
@OptIn(ExperimentalUnsignedTypes::class)
abstract class GetDataCommand<T>(
    sensorCode: UByte,
    private val data: Data<T>
) : ObdCommand<T>(sensorCode) {
    override val pid = ubyteArrayOf(data.pid)

    override val expectedDataBytes = data.expectedBytes

    override fun parseResponse(response: UByteArray) = data.responseParser(response)?.let {
        Result.Success<T, Error>(it)
    } ?: Result.Error(Error.INVALID_RESPONSE)
}
