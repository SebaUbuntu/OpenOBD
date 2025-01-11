/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.themes

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import dev.sebaubuntu.openobd.app.models.Theme
import org.koin.compose.koinInject

/**
 * Default app theme.
 */
@Composable
fun OpenOBDTheme(
    theme: Theme,
    dynamicColors: Boolean,
    content: @Composable () -> Unit,
) {
    val colorSchemeProvider = koinInject<ColorSchemeProvider>()

    val colorScheme = colorSchemeProvider.getColorScheme(theme, dynamicColors)

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
