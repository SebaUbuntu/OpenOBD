/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.obd

import dev.sebaubuntu.openobd.obd.models.DiagnosticTroubleCode

/**
 * OBD service 07: Get pending [DiagnosticTroubleCode]s command.
 */
data object GetPendingDiagnosticTroubleCodesCommand : GetDiagnosticTroubleCodesCommand(0x07u)
