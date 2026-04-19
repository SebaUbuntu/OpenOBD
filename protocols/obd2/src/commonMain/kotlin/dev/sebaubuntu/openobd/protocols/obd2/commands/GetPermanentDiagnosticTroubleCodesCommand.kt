/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.obd2.commands

/**
 * OBD service 0A: Get permanent [DiagnosticTroubleCode]s command.
 */
data object GetPermanentDiagnosticTroubleCodesCommand : GetDiagnosticTroubleCodesCommand(0x0Au)
