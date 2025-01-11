/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

import dev.sebaubuntu.openobd.core.ext.getBit
import dev.sebaubuntu.openobd.core.ext.toUShort
import dev.sebaubuntu.openobd.core.models.value.Temperature
import dev.sebaubuntu.openobd.core.models.value.Temperature.Companion.celsius

data class DieselParticulateFilterTemperature(
    val bank1InletTemperature: Temperature?,
    val bank1OutletTemperature: Temperature?,
    val bank2InletTemperature: Temperature?,
    val bank2OutletTemperature: Temperature?,
) {
    companion object {
        @OptIn(ExperimentalUnsignedTypes::class)
        fun fromObdValue(obdValue: UByteArray): DieselParticulateFilterTemperature {
            val supportedByte = obdValue[0].toUInt()
            val data = obdValue.toList().drop(1).windowed(2)

            val bank1InletTemperature = data[0].takeIf {
                supportedByte.getBit(0)
            }?.toTemperature()
            val bank1OutletTemperature = data[1].takeIf {
                supportedByte.getBit(1)
            }?.toTemperature()
            val bank2InletTemperature = data[2].takeIf {
                supportedByte.getBit(2)
            }?.toTemperature()
            val bank2OutletTemperature = data[3].takeIf {
                supportedByte.getBit(3)
            }?.toTemperature()

            return DieselParticulateFilterTemperature(
                bank1InletTemperature = bank1InletTemperature,
                bank1OutletTemperature = bank1OutletTemperature,
                bank2InletTemperature = bank2InletTemperature,
                bank2OutletTemperature = bank2OutletTemperature,
            )
        }

        @OptIn(ExperimentalUnsignedTypes::class)
        private fun List<UByte>.toTemperature() = this.toUShort().toInt()
            .div(10f)
            .minus(40)
            .celsius
    }
}
