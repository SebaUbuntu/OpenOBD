/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.obd2.commands

/**
 * OBD service 03: Get stored [DiagnosticTroubleCode]s command.
 */
data object GetStoredDiagnosticTroubleCodesCommand : GetDiagnosticTroubleCodesCommand(0x03u)
