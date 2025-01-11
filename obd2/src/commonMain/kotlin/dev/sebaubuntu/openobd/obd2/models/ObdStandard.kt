/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

/**
 * OBD standards.
 */
enum class ObdStandard {
    /**
     * OBD-II as defined by the CARB.
     */
    OBD_II,

    /**
     * OBD as defined by the EPA.
     */
    OBD,

    /**
     * OBD-I.
     */
    OBD_I,

    /**
     * Not OBD compliant.
     */
    NOT_OBD_COMPLIANT,

    /**
     * EOBD (Europe).
     */
    EOBD,

    /**
     * JOBD (Japan).
     */
    JOBD,

    /**
     * Engine Manufacturer Diagnostics (EMD).
     */
    EMD,

    /**
     * Engine Manufacturer Diagnostics Enhanced (EMD+).
     */
    EMD_PLUS,

    /**
     * Heavy Duty On-Board Diagnostics (Child/Partial) (HD OBD-C).
     */
    HD_OBD_C,

    /**
     * Heavy Duty On-Board Diagnostics (HD OBD).
     */
    HD_OBD,

    /**
     * World Wide Harmonized OBD (WWH OBD).
     */
    WWH_OBD,

    /**
     * Heavy Duty Euro OBD Stage I without NOx control (HD EOBD-I).
     */
    HD_EOBD_I,

    /**
     * 	Heavy Duty Euro OBD Stage I with NOx control (HD EOBD-I N).
     */
    HD_EOBD_I_N,

    /**
     * Heavy Duty Euro OBD Stage II without NOx control (HD EOBD-II).
     */
    HD_EOBD_II,

    /**
     * Heavy Duty Euro OBD Stage II with NOx control (HD EOBD-II N).
     */
    HD_EOBD_II_N,

    /**
     * Brazil OBD Phase 1 (OBDBr-1).
     */
    OBDBR_1,

    /**
     * Brazil OBD Phase 2 (OBDBr-2).
     */
    OBDBR_2,

    /**
     * Korean OBD (KOBD).
     */
    KOBD,

    /**
     * India OBD I (IOBD I).
     */
    IOBD_I,

    /**
     * India OBD II (IOBD II).
     */
    IOBD_II,

    /**
     * Heavy Duty Euro OBD Stage VI (HD EOBD-IV).
     */
    HD_EOBD_IV;

    companion object {
        private val obdValueToSet = mapOf(
            0x01u.toUByte() to setOf(OBD_II),
            0x02u.toUByte() to setOf(OBD),
            0x03u.toUByte() to setOf(OBD, OBD_II),
            0x04u.toUByte() to setOf(OBD_I),
            0x05u.toUByte() to setOf(NOT_OBD_COMPLIANT),
            0x06u.toUByte() to setOf(EOBD),
            0x07u.toUByte() to setOf(EOBD, OBD_II),
            0x08u.toUByte() to setOf(EOBD, OBD),
            0x09u.toUByte() to setOf(EOBD, OBD, OBD_II),
            0x0Au.toUByte() to setOf(JOBD),
            0x0Bu.toUByte() to setOf(JOBD, OBD_II),
            0x0Cu.toUByte() to setOf(JOBD, EOBD),
            0x0Du.toUByte() to setOf(JOBD, EOBD, OBD_II),
            0x11u.toUByte() to setOf(EMD),
            0x12u.toUByte() to setOf(EMD_PLUS),
            0x13u.toUByte() to setOf(HD_OBD_C),
            0x14u.toUByte() to setOf(HD_OBD),
            0x15u.toUByte() to setOf(WWH_OBD),
            0x17u.toUByte() to setOf(HD_EOBD_I),
            0x18u.toUByte() to setOf(HD_EOBD_I_N),
            0x19u.toUByte() to setOf(HD_EOBD_II),
            0x1Au.toUByte() to setOf(HD_EOBD_II_N),
            0x1Cu.toUByte() to setOf(OBDBR_1),
            0x1Du.toUByte() to setOf(OBDBR_2),
            0x1Eu.toUByte() to setOf(KOBD),
            0x1Fu.toUByte() to setOf(IOBD_I),
            0x20u.toUByte() to setOf(IOBD_II),
            0x21u.toUByte() to setOf(HD_EOBD_IV),
        )

        fun fromObdValue(obdValue: UByte) = obdValueToSet[obdValue] ?: emptySet()
    }
}
