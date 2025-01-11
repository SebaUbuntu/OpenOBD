/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

/**
 * Fuel type.
 *
 * @param obdValue The OBD value of the fuel type
 */
enum class FuelType(val obdValue: UByte) {
    /**
     * Not available.
     */
    UNKNOWN(0x00u),

    /**
     * Gasoline.
     */
    GASOLINE(0x01u),

    /**
     * Methanol.
     */
    METHANOL(0x02u),

    /**
     * Ethanol.
     */
    ETHANOL(0x03u),

    /**
     * Diesel.
     */
    DIESEL(0x04u),

    /**
     * Liquid Petroleum Gas (LPG).
     */
    LPG(0x05u),

    /**
     * Compressed Natural Gas (CNG).
     */
    CNG(0x06u),

    /**
     * Propane.
     */
    PROPANE(0x07u),

    /**
     * Electric.
     */
    ELECTRIC(0x08u),

    /**
     * Bifuel running gasoline.
     */
    BIFUEL_GASOLINE(0x09u),

    /**
     * Bifuel running methanol.
     */
    BIFUEL_METHANOL(0x0Au),

    /**
     * Bifuel running ethanol.
     */
    BIFUEL_ETHANOL(0x0Bu),

    /**
     * Bifuel running [LPG].
     */
    BIFUEL_LPG(0x0Cu),

    /**
     * Bifuel running [CNG].
     */
    BIFUEL_CNG(0x0Du),

    /**
     * Bifuel running propane.
     */
    BIFUEL_PROPANE(0x0Eu),

    /**
     * Bifuel running electricity.
     */
    BIFUEL_ELECTRIC(0x0Fu),

    /**
     * Bifuel running electric and combustion engine.
     */
    BIFUEL_ELECTRIC_COMBUSTION(0x10u),

    /**
     * Hybrid gasoline.
     */
    HYBRID_GASOLINE(0x11u),

    /**
     * Hybrid ethanol.
     */
    HYBRID_ETHANOL(0x12u),

    /**
     * Hybrid diesel.
     */
    HYBRID_DIESEL(0x13u),

    /**
     * Hybrid electric.
     */
    HYBRID_ELECTRIC(0x14u),

    /**
     * Hybrid running electric and combustion engine.
     */
    HYBRID_ELECTRIC_COMBUSTION(0x15u),

    /**
     * Hybrid regenerative.
     */
    HYBRID_REGENERATIVE(0x16u),

    /**
     * Bifuel running diesel.
     */
    BIFUEL_DIESEL(0x17u);

    companion object {
        fun fromObdValue(obdValue: UByte) = entries.firstOrNull {
            it.obdValue == obdValue
        } ?: error("Unknown fuel type value $obdValue")
    }
}
