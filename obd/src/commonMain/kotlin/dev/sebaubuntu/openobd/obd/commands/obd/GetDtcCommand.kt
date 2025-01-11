/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.obd

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.obd.models.Dtc

/**
 * Get DTC.
 */
@OptIn(ExperimentalUnsignedTypes::class)
data object GetDtcCommand : ObdCommand<List<Dtc>>(0x03u) {
    override val pid = ubyteArrayOf()

    override fun parseResponse(response: UByteArray): Result<List<Dtc>, Error> {
        TODO("Not yet implemented")
    }
}
