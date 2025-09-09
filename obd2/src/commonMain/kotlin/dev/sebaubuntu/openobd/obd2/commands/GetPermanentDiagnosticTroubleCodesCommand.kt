/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd2.commands

import dev.sebaubuntu.openobd.obd2.models.DiagnosticTroubleCode

/**
 * OBD service 0A: Get permanent [DiagnosticTroubleCode]s command.
 */
data object GetPermanentDiagnosticTroubleCodesCommand : GetDiagnosticTroubleCodesCommand(0x0Au)
