/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.elm327.commands

import dev.sebaubuntu.openobd.core.models.Result

@OptIn(ExperimentalUnsignedTypes::class)
data class RawCanCommand(
    val data: List<UByte>,
) : CanCommand<UByteArray>() {
    override val commandBytes = data.toUByteArray()

    override fun parseControlModuleResponse(
        rawResponse: UByteArray
    ) = Result.Success(rawResponse)
}
