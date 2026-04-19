/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.obd2.commands

/**
 * OBD service 07: Get pending [DiagnosticTroubleCode]s command.
 */
data object GetPendingDiagnosticTroubleCodesCommand : GetDiagnosticTroubleCodesCommand(0x07u)
