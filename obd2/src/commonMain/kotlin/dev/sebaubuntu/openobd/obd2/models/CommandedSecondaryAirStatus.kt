/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

/**
 * Commanded secondary air status.
 *
 * @param obdValue The OBD value of the commanded secondary air status
 */
enum class CommandedSecondaryAirStatus(val obdValue: UByte) {
    /**
     * Upstream.
     */
    UPSTREAM(0x01u),

    /**
     * Downstream of catalytic converter.
     */
    DOWNSTREAM_OF_CATALYTIC_CONVERTER(0x02u),

    /**
     * From the outside atmosphere or off.
     */
    FROM_OUTSIDE_ATMOSPHERE_OR_OFF(0x04u),

    /**
     * Pump commanded on for diagnostics.
     */
    PUMP_COMMANDED(0x08u);

    companion object {
        fun fromObdValue(obdValue: UByte) = entries.firstOrNull {
            it.obdValue == obdValue
        } ?: error("Unknown commanded secondary air status value $obdValue")
    }
}
