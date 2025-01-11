/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.themes

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import dev.sebaubuntu.openobd.app.models.Theme

class AndroidColorSchemeProvider(
    private val context: Context,
) : ColorSchemeProvider {
    override val supportsDynamicColors = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    @Composable
    override fun getColorScheme(
        theme: Theme,
        dynamicColors: Boolean
    ) = when (theme) {
        Theme.SYSTEM -> when (isSystemInDarkTheme()) {
            true -> darkColorScheme(dynamicColors)
            false -> lightColorScheme(dynamicColors)
        }

        Theme.DARK -> darkColorScheme(dynamicColors)
        Theme.LIGHT -> lightColorScheme(dynamicColors)
    }

    @Composable
    private fun lightColorScheme(
        dynamicColors: Boolean,
    ) = if (dynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicLightColorScheme(context)
    } else {
        lightColorScheme()
    }

    @Composable
    private fun darkColorScheme(
        dynamicColors: Boolean,
    ) = if (dynamicColors && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        dynamicDarkColorScheme(context)
    } else {
        darkColorScheme()
    }
}
