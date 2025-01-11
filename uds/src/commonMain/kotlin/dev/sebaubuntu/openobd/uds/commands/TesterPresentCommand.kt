/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.uds.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result

/**
 * UDS service 3E: Tester present command.
 */
@OptIn(ExperimentalUnsignedTypes::class)
data object TesterPresentCommand : UdsCommand<Unit>(0x3Eu) {
    override val subfunction: UByte = 0x00u
    override fun parseResponse(response: UByteArray) = Result.Success<_, Error>(Unit)
}
