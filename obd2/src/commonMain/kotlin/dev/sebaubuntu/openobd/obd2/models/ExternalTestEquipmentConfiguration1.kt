/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

import dev.sebaubuntu.openobd.core.models.value.Current
import dev.sebaubuntu.openobd.core.models.value.Current.Companion.milliamperes
import dev.sebaubuntu.openobd.core.models.value.Pressure
import dev.sebaubuntu.openobd.core.models.value.Pressure.Companion.kilopascals
import dev.sebaubuntu.openobd.core.models.value.Voltage
import dev.sebaubuntu.openobd.core.models.value.Voltage.Companion.volts

data class ExternalTestEquipmentConfiguration1(
    val equivalenceRatioMaxValue: Int,
    val oxygenSensorMaxVoltage: Voltage,
    val oxygenSensorMaxCurrent: Current,
    val intakeManifoldMaxAbsolutePressure: Pressure,
) {
    companion object {
        @OptIn(ExperimentalUnsignedTypes::class)
        fun fromObdValue(obdValue: UByteArray): ExternalTestEquipmentConfiguration1 {
            val equivalenceRatioMaxValue = obdValue[0].toInt()
            val oxygenSensorMaxVoltage = obdValue[1].toShort().volts
            val oxygenSensorMaxCurrent = obdValue[2].toShort().milliamperes
            val intakeManifoldMaxAbsolutePressure = obdValue[3].toShort()
                .times(10)
                .kilopascals

            return ExternalTestEquipmentConfiguration1(
                equivalenceRatioMaxValue = equivalenceRatioMaxValue,
                oxygenSensorMaxVoltage = oxygenSensorMaxVoltage,
                oxygenSensorMaxCurrent = oxygenSensorMaxCurrent,
                intakeManifoldMaxAbsolutePressure = intakeManifoldMaxAbsolutePressure,
            )
        }
    }
}
