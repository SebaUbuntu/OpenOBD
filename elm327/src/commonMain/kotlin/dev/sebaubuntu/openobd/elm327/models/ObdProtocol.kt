/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.elm327.models

/**
 * OBD protocols.
 *
 * @param elm327Value The value used by the ELM327 to set the protocol
 */
enum class ObdProtocol(val elm327Value: Int) {
    /**
     * Automatic protocol selection.
     */
    AUTOMATIC(0),

    /**
     * SAE J1850 PWM (41.6 kbaud).
     */
    SAE_J1850_PWM(1),

    /**
     * SAE J1850 VPW (10.4 kbaud).
     */
    SAE_J1850_VPW(2),

    /**
     * ISO 9141-2 (5 baud init, 10.4 kbaud).
     */
    ISO_9141_2(3),

    /**
     * ISO 14230-4 KWP (5 baud init, 10.4 kbaud)
     */
    ISO_14230_4_KWP(4),

    /**
     * ISO 14230-4 KWP (fast init, 10.4 kbaud).
     */
    ISO_14230_4_KWP_FAST(5),

    /**
     * ISO 15765-4 CAN (11 bit ID, 500 kbaud).
     */
    ISO_15765_4_CAN_11_500(6),

    /**
     * ISO 15765-4 CAN (29 bit ID, 500 kbaud).
     */
    ISO_15765_4_CAN_29_500(7),

    /**
     * ISO 15765-4 CAN (11 bit ID, 250 kbaud).
     */
    ISO_15765_4_CAN_11_250(8),

    /**
     * ISO 15765-4 CAN (29 bit ID, 250 kbaud).
     */
    ISO_15765_4_CAN_29_250(9),

    /**
     * SAE J1939 CAN (29 bit ID, 250 kbaud).
     */
    SAE_J1939(10),

    /**
     * USER1 CAN (11 bit ID, 125 kbaud).
     */
    USER1_CAN(11),

    /**
     * USER2 CAN (11 bit ID, 50 kbaud).
     */
    USER2_CAN(12),
}
