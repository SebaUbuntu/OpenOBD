/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.obd

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.obd.models.VehicleInformationType

/**
 * Get vehicle information command.
 */
@OptIn(ExperimentalUnsignedTypes::class)
class GetVehicleInformationCommand<T>(
    private val vehicleInformationType: VehicleInformationType<T>,
) : ObdCommand<T>(0x09u) {
    override val pid = ubyteArrayOf(vehicleInformationType.parameterId)

    override val expectedDataBytes = vehicleInformationType.expectedBytes

    override fun parseResponse(
        response: UByteArray,
    ) = Result.Success<T, Error>(vehicleInformationType.responseParser(response))
}
