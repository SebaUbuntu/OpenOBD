/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.obd

import dev.sebaubuntu.openobd.obd.models.DiagnosticTroubleCode

/**
 * OBD service 03: Get stored [DiagnosticTroubleCode]s command.
 */
data object GetStoredDiagnosticTroubleCodesCommand : GetDiagnosticTroubleCodesCommand(0x03u)
