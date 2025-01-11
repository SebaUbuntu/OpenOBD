/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SupportedParameterIdsTest {
    @Test
    fun randomValue() {
        val supportedParameterIds = SupportedParameterIds.fromObdValue(
            0xBE1FA813u,
            0x00u,
        )

        val expectedValues = setOf<UByte>(
            0x01u,
            0x03u,
            0x04u,
            0x05u,
            0x06u,
            0x07u,
            0x0Cu,
            0x0Du,
            0x0Eu,
            0x0Fu,
            0x10u,
            0x11u,
            0x13u,
            0x15u,
            0x1Cu,
            0x1Fu,
            0x20u,
        )

        expectedValues.forEach {
            assertTrue(
                it in supportedParameterIds,
                "Parameter ID $it not in supported parameter IDs"
            )
        }

        assertEquals(expectedValues, supportedParameterIds)
    }
}
