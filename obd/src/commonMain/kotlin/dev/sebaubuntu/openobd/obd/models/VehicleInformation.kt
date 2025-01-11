/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.models

@OptIn(ExperimentalUnsignedTypes::class)
data class VehicleInformation<T>(
    val pid: UByte,
    val responseParser: (UByteArray) -> T?,
) {
    companion object {
        /**
         * VIN Message Count in PID [VIN]. Only for ISO 9141-2, ISO 14230-4 and SAE J1850.
         */
        val VIN_MESSAGE_COUNT = VehicleInformation(0x01u) {
            it[0].toInt()
        }

        /**
         * Vehicle Identification Number (VIN).
         */
        val VIN = VehicleInformation(0x02u) {
            it.toByteArray().decodeToString()
        }

        /**
         * ECU name message count for PID [ECU_NAME].
         */
        val ECU_NAME_MESSAGE_COUNT = VehicleInformation(0x09u) {
            it[0].toInt()
        }

        /**
         * ECU name.
         */
        val ECU_NAME = VehicleInformation(0x0Au) {
            it.toByteArray().decodeToString()
        }
    }
}
