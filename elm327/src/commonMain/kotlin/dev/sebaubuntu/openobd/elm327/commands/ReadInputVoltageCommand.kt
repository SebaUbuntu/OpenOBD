/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.value.Voltage
import dev.sebaubuntu.openobd.core.models.value.Voltage.Companion.volts
import dev.sebaubuntu.openobd.elm327.Command

/**
 * Read the input voltage.
 */
object ReadInputVoltageCommand : Command<Voltage> {
    private val voltageRegex = "([0-9]+[.][0-9]+)V".toRegex()

    override val command = "AT RV"
    override fun parseResponse(response: List<String>) = response.getOrNull(0)?.let {
        voltageRegex.matchEntire(it)?.let { matchResult ->
            val (voltage) = matchResult.destructured

            Result.Success<_, Error>(voltage.toFloat().volts)
        }
    } ?: Result.Error(Error.INVALID_RESPONSE)
}
