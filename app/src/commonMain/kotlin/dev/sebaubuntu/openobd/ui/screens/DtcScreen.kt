/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import dev.sebaubuntu.openobd.repositories.ObdRepository
import dev.sebaubuntu.openobd.ui.composables.ConnectionGatedComposable

/**
 * DTC error codes screen.
 */
@Composable
fun DtcScreen(
    paddingValues: PaddingValues,
    obdRepository: ObdRepository,
) {
    ConnectionGatedComposable(
        obdRepository = obdRepository,
        paddingValues = paddingValues,
    ) {

    }
}
