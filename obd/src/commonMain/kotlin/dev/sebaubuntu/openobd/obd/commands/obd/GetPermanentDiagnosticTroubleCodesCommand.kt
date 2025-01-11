/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.obd

import dev.sebaubuntu.openobd.obd.models.DiagnosticTroubleCode

/**
 * OBD service 0A: Get permanent [DiagnosticTroubleCode]s command.
 */
data object GetPermanentDiagnosticTroubleCodesCommand : GetDiagnosticTroubleCodesCommand(0x0Au)
