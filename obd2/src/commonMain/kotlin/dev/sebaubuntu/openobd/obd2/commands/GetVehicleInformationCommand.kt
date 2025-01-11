/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.obd2.models.VehicleInformationType

/**
 * OBD service 09: Get vehicle information command.
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
