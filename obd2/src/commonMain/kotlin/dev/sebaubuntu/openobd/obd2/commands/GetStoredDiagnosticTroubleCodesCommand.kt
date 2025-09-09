/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd2.commands

import dev.sebaubuntu.openobd.obd2.models.DiagnosticTroubleCode

/**
 * OBD service 03: Get stored [DiagnosticTroubleCode]s command.
 */
data object GetStoredDiagnosticTroubleCodesCommand : GetDiagnosticTroubleCodesCommand(0x03u)
