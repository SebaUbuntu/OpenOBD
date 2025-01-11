/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.elm327.commands

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.value.Value.Companion.asValue
import dev.sebaubuntu.openobd.core.models.value.Voltage
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

            Result.Success<_, Error>(voltage.toFloat().asValue(Voltage.Unit.VOLT))
        }
    } ?: Result.Error(Error.INVALID_RESPONSE)
}
