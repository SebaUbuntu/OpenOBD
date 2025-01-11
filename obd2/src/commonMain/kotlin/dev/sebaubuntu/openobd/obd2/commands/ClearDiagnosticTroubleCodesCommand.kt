/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result

/**
 * OBD service 04: Clear DTC.
 */
@OptIn(ExperimentalUnsignedTypes::class)
data object ClearDiagnosticTroubleCodesCommand : ObdCommand<Unit>(0x04u) {
    override val pid = ubyteArrayOf()

    override fun parseResponse(response: UByteArray) = Result.Success<_, Error>(Unit)
}
