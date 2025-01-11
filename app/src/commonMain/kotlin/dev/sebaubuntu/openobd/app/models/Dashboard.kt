/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.models

import dev.sebaubuntu.openobd.core.models.value.Frequency
import dev.sebaubuntu.openobd.core.models.value.Frequency.Companion.revolutionsPerMinute
import dev.sebaubuntu.openobd.core.models.value.Length
import dev.sebaubuntu.openobd.core.models.value.Length.Companion.kilometers
import dev.sebaubuntu.openobd.core.models.value.Percentage
import dev.sebaubuntu.openobd.core.models.value.Percentage.Companion.percent
import dev.sebaubuntu.openobd.core.models.value.Speed
import dev.sebaubuntu.openobd.core.models.value.Speed.Companion.kilometersPerHour
import dev.sebaubuntu.openobd.core.models.value.Temperature
import dev.sebaubuntu.openobd.core.models.value.Temperature.Companion.celsius

/**
 * Dashboard state.
 *
 * @param vehicleSpeed Vehicle speed
 * @param maxVehicleSpeed Maximum vehicle speed
 * @param engineSpeed Engine speed
 * @param maxEngineSpeed Maximum engine speed
 * @param odometer Odometer
 * @param fuelLevel Fuel level in percent
 * @param engineCoolantTemperature Engine coolant temperature
 * @param engineOilTemperature Engine oil temperature
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
        ENGINE_OIL_PRESSURE,

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
            vehicleSpeed = 0f.kilometersPerHour,
            maxVehicleSpeed = 220f.kilometersPerHour,
            engineSpeed = 0f.revolutionsPerMinute,
            maxEngineSpeed = 6000f.revolutionsPerMinute,
            odometer = 0f.kilometers,
            fuelLevel = 50.percent,
            engineCoolantTemperature = 0f.celsius,
            engineOilTemperature = 0f.celsius,
            transmissionGear = TransmissionGear.Automatic.NEUTRAL,
            lights = setOf(
                Light.CHECK_ENGINE,
                Light.ENGINE_OIL_PRESSURE,
                Light.BATTERY,
            ),
        )
    }
}
