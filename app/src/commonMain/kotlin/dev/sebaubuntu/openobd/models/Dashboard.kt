/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

/**
 * Dashboard state.
 *
 * @param speedKph Speed in km/h
 * @param maxSpeedKph Maximum speed in km/h
 * @param rpm Revolutions per minute
 * @param maxRpm Maximum revolutions per minute
 * @param odometer Odometer in kilometers
 * @param fuelLevel Fuel level in percent, from 0.0 to 1.0
 * @param coolantTemperatureCelsius Coolant temperature in Celsius
 * @param lights Lights
 */
data class Dashboard(
    val speedKph: Int,
    val maxSpeedKph: Int,
    val rpm: Int,
    val maxRpm: Int,
    val odometer: Int,
    val fuelLevel: Float,
    val coolantTemperatureCelsius: Int,
    val engineOilTemperatureCelsius: Int,
    val transmissionGear: TransmissionGear,
    val lights: Set<Light>,
) {
    /**
     * Transmission gear.
     */
    sealed interface TransmissionGear {
        val transmissionType: TransmissionType

        enum class Automatic : TransmissionGear {
            PARK,
            REVERSE,
            NEUTRAL,
            DRIVE;

            override val transmissionType = TransmissionType.AUTOMATIC
        }

        /**
         * Manual transmission gear.
         *
         * @param position Manual transmission gear position.
         */
        data class Manual(val position: Int) : TransmissionGear {
            override val transmissionType = TransmissionType.MANUAL

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
            speedKph = 0,
            maxSpeedKph = 220,
            rpm = 0,
            maxRpm = 6000,
            odometer = 0,
            fuelLevel = 0.5f,
            coolantTemperatureCelsius = 0,
            engineOilTemperatureCelsius = 0,
            transmissionGear = TransmissionGear.Automatic.NEUTRAL,
            lights = setOf(
                Light.CHECK_ENGINE,
                Light.OIL_PRESSURE,
                Light.BATTERY,
            ),
        )
    }
}
