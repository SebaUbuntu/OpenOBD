/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.obd

import dev.sebaubuntu.openobd.core.ext.toUShort
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.obd.models.DiagnosticTroubleCode

/**
 * Get [DiagnosticTroubleCode]s base command.
 */
@OptIn(ExperimentalUnsignedTypes::class)
sealed class GetDiagnosticTroubleCodesCommand(
    serviceCode: UByte,
) : ObdCommand<List<DiagnosticTroubleCode>>(serviceCode) {
    override val pid = ubyteArrayOf()

    override fun parseResponse(response: UByteArray) = when (response.size % 2 == 0) {
        true -> Result.Success<_, Error>(
            response.chunked(2).map {
                DiagnosticTroubleCode(it.toUShort())
            }
        )

        false -> {
            Logger.error(this::class.simpleName) { "Invalid response size: ${response.size}" }
            Result.Error(Error.INVALID_RESPONSE)
        }
    }
}
