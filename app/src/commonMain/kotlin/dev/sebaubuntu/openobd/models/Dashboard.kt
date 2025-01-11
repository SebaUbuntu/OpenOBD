/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

import dev.sebaubuntu.openobd.core.models.value.Frequency
import dev.sebaubuntu.openobd.core.models.value.Length
import dev.sebaubuntu.openobd.core.models.value.Percentage
import dev.sebaubuntu.openobd.core.models.value.Speed
import dev.sebaubuntu.openobd.core.models.value.Temperature
import dev.sebaubuntu.openobd.core.models.value.Value.Companion.asValue

/**
 * Dashboard state.
 *
 * @param vehicleSpeed Vehicle speed
 * @param maxVehicleSpeed Maximum vehicle speed
 * @param engineSpeed Engine speed
 * @param maxEngineSpeed Maximum engine speed
 * @param odometer Odometer in kilometers
 * @param fuelLevel Fuel level in percent, from 0.0 to 1.0
 * @param engineCoolantTemperature Coolant temperature in Celsius
 * @param lights Lights
 */
data class Dashboard(
    val vehicleSpeed: Speed,
    val maxVehicleSpeed: Speed,
    val engineSpeed: Frequency,
    val maxEngineSpeed: Frequency,
    val odometer: Length,
    val fuelLevel: Percentage,
    val engineCoolantTemperature: Temperature,
    val engineOilTemperature: Temperature,
    val transmissionGear: TransmissionGear,
    val lights: Set<Light>,
) {
    /**
     * Transmission gear.
     */
    sealed interface TransmissionGear {
        enum class Automatic : TransmissionGear {
            PARK,
            REVERSE,
            NEUTRAL,
            DRIVE,
        }

        /**
         * Manual transmission gear.
         *
         * @param gear Manual transmission gear
         */
        data class Manual(val gear: Int) : TransmissionGear {
            companion object {
                const val NEUTRAL = 0
                const val REVERSE = -1
            }
        }
    }

    enum class Light {
        /**
         * Check engine warning.
         */
        CHECK_ENGINE,

        /**
         * Low oil pressure warning.
         */
        OIL_PRESSURE,

        /**
         * Engine coolant temperature warning.
         */
        ENGINE_COOLANT_TEMPERATURE,

        /**
         * Traction control off.
         */
        ESP,

        /**
         * Battery not charging.
         */
        BATTERY,

        /**
         * Anti-Lock Braking System (ABS) warning.
         */
        ABS,

        /**
         * Tire Pressure Monitoring System (TPMS) warning.
         */
        TPMS,

        /**
         * Airbag issue.
         */
        AIRBAG,

        /**
         * Low fuel warning.
         */
        LOW_FUEL,

        /**
         * Low washer fluid warning.
         */
        LOW_WASHER_FLUID,

        /**
         * Seat belt reminder.
         */
        SEAT_BELT_REMINDER,

        /**
         * Immobilizer active.
         */
        IMMOBILIZER,

        /**
         * Transmission temperature warning.
         */
        TRANSMISSION_TEMPERATURE,

        /**
         * Parking brake engaged.
         */
        PARKING_BRAKE,

        /**
         * Brake warning.
         */
        BRAKE,

        /**
         * Doors open warning.
         */
        DOORS_OPEN,

        /**
         * Left turn signal.
         */
        LEFT_TURN,

        /**
         * Right turn signal.
         */
        RIGHT_TURN,

        /**
         * Position lights.
         */
        POSITION_LIGHTS,

        /**
         * Low beam lights.
         */
        LOW_BEAM_LIGHTS,

        /**
         * High beam lights.
         */
        HIGH_BEAM_LIGHTS,

        /**
         * Front fog lights.
         */
        FRONT_FOG_LIGHTS,

        /**
         * Rear fog lights.
         */
        REAR_FOG_LIGHTS,
    }

    companion object {
        val DEFAULT = Dashboard(
            vehicleSpeed = 0f.asValue(Speed.Unit.KILOMETER_PER_HOUR),
            maxVehicleSpeed = 220f.asValue(Speed.Unit.KILOMETER_PER_HOUR),
            engineSpeed = 0f.asValue(Frequency.Unit.REVOLUTION_PER_MINUTE),
            maxEngineSpeed = 6000f.asValue(Frequency.Unit.REVOLUTION_PER_MINUTE),
            odometer = 0f.asValue(Length.Unit.KILOMETER),
            fuelLevel = 0.5f.asValue(Percentage.Unit.PERCENT),
            engineCoolantTemperature = 0f.asValue(Temperature.Unit.CELSIUS),
            engineOilTemperature = 0f.asValue(Temperature.Unit.CELSIUS),
            transmissionGear = TransmissionGear.Automatic.NEUTRAL,
            lights = setOf(
                Light.CHECK_ENGINE,
                Light.OIL_PRESSURE,
                Light.BATTERY,
            ),
        )
    }
}
