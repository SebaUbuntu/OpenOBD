/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.obd

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.obd.models.VehicleInformation

/**
 * Get vehicle information command.
 */
@OptIn(ExperimentalUnsignedTypes::class)
class GetVehicleInformationCommand<T>(
    private val vehicleInformation: VehicleInformation<T>,
) : ObdCommand<T>(0x09u) {
    override val pid = ubyteArrayOf(vehicleInformation.pid)

    override fun parseResponse(
        response: UByteArray,
    ) = vehicleInformation.responseParser(response)?.let {
        Result.Success<T, Error>(it)
    } ?: Result.Error(Error.INVALID_RESPONSE)
}
