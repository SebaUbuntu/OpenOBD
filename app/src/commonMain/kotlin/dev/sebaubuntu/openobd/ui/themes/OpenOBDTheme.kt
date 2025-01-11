/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/**
 * Default app theme.
 */
@Composable
fun OpenOBDTheme(content: @Composable () -> Unit) {
    val colorScheme = when (isSystemInDarkTheme()) {
        true -> darkColorScheme()
        false -> lightColorScheme()
    }

    MaterialTheme(colorScheme = colorScheme) {
        content()
    }
}
