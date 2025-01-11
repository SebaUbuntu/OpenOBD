/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd2.models

import dev.sebaubuntu.openobd.core.models.value.MassFlowRate
import dev.sebaubuntu.openobd.core.models.value.Value.Companion.asValue

data class ExternalTestEquipmentConfiguration2(
    val massAirFlowMaxAirFlowRate: MassFlowRate,
) {
    companion object {
        @OptIn(ExperimentalUnsignedTypes::class)
        fun fromObdValue(obdValue: UByteArray): ExternalTestEquipmentConfiguration2 {
            val massAirFlowMaxAirFlowRate = obdValue[0].toInt()
                .times(10)
                .asValue(MassFlowRate.Unit.GRAM_PER_SECOND)

            return ExternalTestEquipmentConfiguration2(
                massAirFlowMaxAirFlowRate = massAirFlowMaxAirFlowRate,
            )
        }
    }
}
