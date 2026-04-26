/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.obd2.commands

import dev.sebaubuntu.openobd.network.obd2.models.DiagnosticTroubleCode

/**
 * OBD service 07: Get pending [DiagnosticTroubleCode]s command.
 */
data object GetPendingDiagnosticTroubleCodesCommand : GetDiagnosticTroubleCodesCommand(0x07u)
