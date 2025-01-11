/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.uds.models

/**
 * ECU reset types.
 */
enum class ResetType(val subfunction: UByte) {
    HARD_RESET(0x01u),
    KEY_OFF_ON_RESET(0x02u),
    SOFT_RESET(0x03u),
    ENABLE_RAPID_POWER_SHUT_DOWN(0x04u),
    DISABLE_RAPID_POWER_SHUT_DOWN(0x05u),
}
