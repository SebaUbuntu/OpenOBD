/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.commands

import dev.sebaubuntu.openobd.core.ext.toUShort
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.obd2.models.DiagnosticTroubleCode

/**
 * Get [DiagnosticTroubleCode]s base command.
 */
@OptIn(ExperimentalUnsignedTypes::class)
sealed class GetDiagnosticTroubleCodesCommand(
    serviceCode: UByte,
) : ObdCommand<List<DiagnosticTroubleCode>>(serviceCode) {
    override val pid = ubyteArrayOf()

    override fun parseResponse(
        response: UByteArray,
    ): Result<List<DiagnosticTroubleCode>, Error> {
        if (response.isEmpty()) {
            Logger.error(this::class.simpleName) { "Empty response" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        val codesCount = response[0].toInt()
        val codes = response.drop(1)

        if (codes.size % 2 != 0) {
            Logger.error(this::class.simpleName) { "Odd number of bytes in response" }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        val actualCodesCount = codes.size / 2

        if (codesCount != actualCodesCount) {
            Logger.error(this::class.simpleName) {
                "Declared codes count ($codesCount) does not match actual codes count (${actualCodesCount})"
            }
            return Result.Error(Error.INVALID_RESPONSE)
        }

        return Result.Success(
            codes.chunked(2) {
                DiagnosticTroubleCode(it.toUShort())
            }
        )
    }
}
