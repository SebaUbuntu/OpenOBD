/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

import dev.sebaubuntu.openobd.core.models.value.MassFlowRate
import dev.sebaubuntu.openobd.core.models.value.MassFlowRate.Companion.gramsPerSecond

data class ExternalTestEquipmentConfiguration2(
    val massAirFlowMaxAirFlowRate: MassFlowRate,
) {
    companion object {
        @OptIn(ExperimentalUnsignedTypes::class)
        fun fromObdValue(obdValue: UByteArray): ExternalTestEquipmentConfiguration2 {
            val massAirFlowMaxAirFlowRate = obdValue[0].toInt()
                .times(10)
                .gramsPerSecond

            return ExternalTestEquipmentConfiguration2(
                massAirFlowMaxAirFlowRate = massAirFlowMaxAirFlowRate,
            )
        }
    }
}
