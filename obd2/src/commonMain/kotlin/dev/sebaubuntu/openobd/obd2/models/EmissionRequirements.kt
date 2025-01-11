/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

/**
 * Emissions requirements.
 *
 * @param obdValue The OBD value corresponding to this emissions requirements
 */
enum class EmissionRequirements(val obdValue: UByte) {
    /**
     * Heavy Duty Vehicles (EURO IV) B1.
     */
    HEAVY_DUTY_VEHICLES_EURO_IV_B1(0x0Eu),

    /**
     * Heavy Duty Vehicles (EURO V) B2.
     */
    HEAVY_DUTY_VEHICLES_EURO_V_B2(0x0Fu),

    /**
     * Heavy Duty Vehicles (EURO EEV) C.
     */
    HEAVY_DUTY_VEHICLES_EURO_EEV_C(0x10u);

    companion object {
        fun fromObdValue(obdValue: UByte) = entries.firstOrNull {
            it.obdValue == obdValue
        } ?: error("Unknown emissions requirements value: $obdValue")
    }
}
