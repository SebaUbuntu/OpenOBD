/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

/**
 * Fuel system status.
 *
 * @param obdValue The OBD value of the fuel system status
 */
enum class FuelSystemStatus(val obdValue: UByte) {
    /**
     * The motor is off.
     */
    MOTOR_OFF(0x00u),

    /**
     * Open loop due to insufficient engine temperature.
     */
    OPEN_LOOP_DUE_TO_INSUFFICIENT_ENGINE_TEMPERATURE(0x01u),

    /**
     * Closed loop, using oxygen sensor feedback to determine fuel mix.
     */
    CLOSED_LOOP_USING_OXYGEN_SENSOR_FEEDBACK_TO_DETERMINE_FUEL_MIX(0x02u),

    /**
     * Open loop due to engine load OR fuel cut due to deceleration.
     */
    OPEN_LOOP_DUE_TO_ENGINE_LOAD_OR_FUEL_CUT_DUE_TO_DECELERATION(0x04u),

    /**
     * Open loop due to system failure.
     */
    OPEN_LOOP_DUE_TO_SYSTEM_FAILURE(0x08u),

    /**
     * Closed loop, using at least one oxygen sensor but there is a fault in the feedback system.
     */
    CLOSED_LOOP_USING_AT_LEAST_ONE_OXYGEN_SENSOR_BUT_THERE_IS_A_FAULT_IN_THE_FEEDBACK_SYSTEM(0x10u);

    companion object {
        fun fromObdValue(obdValue: UByte) = entries.firstOrNull {
            it.obdValue == obdValue
        } ?: error("Unknown fuel system status value $obdValue")
    }
}
