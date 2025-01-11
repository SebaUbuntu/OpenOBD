/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.models

import dev.sebaubuntu.openobd.core.models.Value

/**
 * OBD data.
 *
 * @param pid The PID of the sensor
 * @param responseParser The parser for the response, returns null on failed parsing
 */
@OptIn(ExperimentalUnsignedTypes::class)
data class Data<T>(
    val pid: UByte,
    val expectedBytes: Int? = null,
    val responseParser: (UByteArray) -> T?,
) {
    companion object {
        /**
         * Calculated engine load.
         */
        val CALCULATED_ENGINE_LOAD = Data(
            pid = 0x04u,
            expectedBytes = 1,
        ) {
            Value.Percentage(it[0].toInt().div(255f), Value.Percentage.Unit.PERCENT)
        }

        /**
         * Engine coolant temperature.
         */
        val ENGINE_COOLANT_TEMPERATURE = Data(
            pid = 0x05u,
            expectedBytes = 1,
        ) {
            Value.Temperature(
                it[0].toInt().minus(40).toFloat(),
                Value.Temperature.Unit.CELSIUS,
            )
        }

        /**
         * Engine speed.
         */
        val ENGINE_SPEED = Data(
            pid = 0x0Cu,
            expectedBytes = 2,
        ) {
            Value.Frequency(
                it[0].toInt().times(256)
                    .plus(it[1].toInt())
                    .div(4f),
                Value.Frequency.Unit.REVOLUTIONS_PER_MINUTE,
            )
        }

        /**
         * Vehicle speed.
         */
        val VEHICLE_SPEED = Data(
            pid = 0x0Du,
            expectedBytes = 1,
        ) {
            Value.Speed(it[0].toFloat(), Value.Speed.Unit.KILOMETER_PER_HOUR)
        }

        /**
         * Intake air temperature.
         */
        val INTAKE_AIR_TEMPERATURE = Data(
            pid = 0x0Fu,
            expectedBytes = 1,
        ) {
            Value.Temperature(
                it[0].toInt().minus(40).toFloat(),
                Value.Temperature.Unit.CELSIUS,
            )
        }

        /**
         * Throttle position.
         */
        val THROTTLE_POSITION = Data(
            pid = 0x11u,
            expectedBytes = 1,
        ) {
            Value.Percentage(it[0].toInt().div(255f), Value.Percentage.Unit.PERCENT)
        }

        /**
         * Ambient air temperature.
         */
        val AMBIENT_AIR_TEMPERATURE = Data(
            pid = 0x46u,
            expectedBytes = 1,
        ) {
            Value.Temperature(
                it[0].toInt().minus(40).toFloat(),
                Value.Temperature.Unit.CELSIUS,
            )
        }

        /**
         * Fuel type.
         */
        val FUEL_TYPE = Data(
            pid = 0x51u,
            expectedBytes = 1,
        ) {
            FuelType.fromObdValue(it[0])
        }

        /**
         * Engine oil temperature.
         */
        val ENGINE_OIL_TEMPERATURE = Data(
            pid = 0x5Cu,
            expectedBytes = 1,
        ) {
            Value.Temperature(
                it[0].toInt().minus(40).toFloat(),
                Value.Temperature.Unit.CELSIUS,
            )
        }

        /**
         * Diesel particulate filter (DPF) temperature.
         */
        val DPF_TEMPERATURE = Data(
            pid = 0x7Cu,
            expectedBytes = 2,
        ) {
            Value.Temperature(
                it[0].toInt().times(256)
                    .plus(it[1].toInt())
                    .div(10f)
                    .minus(40),
                Value.Temperature.Unit.CELSIUS,
            )
        }

        /**
         * Odometer.
         */
        val ODOMETER = Data(
            pid = 0xA6u,
            expectedBytes = 4,
        ) {
            Value.Length(
                it[0].toInt().shl(24)
                    .plus(it[1].toInt().shl(16))
                    .plus(it[2].toInt().shl(8))
                    .plus(it[3].toInt())
                    .div(10f),
                Value.Length.Unit.KILOMETER,
            )
        }
    }
}
