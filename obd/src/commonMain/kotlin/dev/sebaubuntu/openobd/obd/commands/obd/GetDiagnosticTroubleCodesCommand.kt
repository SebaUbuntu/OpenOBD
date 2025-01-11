/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.obd

import dev.sebaubuntu.openobd.core.ext.toUShort
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Result.Companion.flatMap
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.obd.models.DiagnosticTroubleCode
import dev.sebaubuntu.openobd.obd.utils.IsoTpDecoder

/**
 * Get DTCs.
 */
@OptIn(ExperimentalUnsignedTypes::class)
data object GetDiagnosticTroubleCodesCommand : ObdCommand<List<DiagnosticTroubleCode>>(0x03u) {
    private val LOG_TAG = GetDiagnosticTroubleCodesCommand::class.simpleName!!

    override val pid = ubyteArrayOf()

    override fun parseResponse(
        response: UByteArray,
    ) = IsoTpDecoder.parse(response, 4).flatMap { data ->
        if (data.size % 2 != 0) {
            Logger.error(LOG_TAG) { "Invalid response size: ${data.size}" }
            Result.Error(Error.INVALID_RESPONSE)
        } else {
            Result.Success(
                data.chunked(2).map {
                    DiagnosticTroubleCode(it.toUByteArray().toUShort())
                }
            )
        }
    }
}
