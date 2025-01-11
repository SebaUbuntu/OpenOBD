/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd2.models

import dev.sebaubuntu.openobd.core.models.value.Current
import dev.sebaubuntu.openobd.core.models.value.Pressure
import dev.sebaubuntu.openobd.core.models.value.Value.Companion.asValue
import dev.sebaubuntu.openobd.core.models.value.Voltage

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
            val oxygenSensorMaxVoltage = obdValue[1].toShort().asValue(Voltage.Unit.VOLT)
            val oxygenSensorMaxCurrent = obdValue[2].toShort().asValue(Current.Unit.MILLIAMPERE)
            val intakeManifoldMaxAbsolutePressure = obdValue[3].toShort()
                .times(10)
                .asValue(Pressure.Unit.KILOPASCAL)

            return ExternalTestEquipmentConfiguration1(
                equivalenceRatioMaxValue = equivalenceRatioMaxValue,
                oxygenSensorMaxVoltage = oxygenSensorMaxVoltage,
                oxygenSensorMaxCurrent = oxygenSensorMaxCurrent,
                intakeManifoldMaxAbsolutePressure = intakeManifoldMaxAbsolutePressure,
            )
        }
    }
}
