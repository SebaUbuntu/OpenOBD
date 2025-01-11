/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.themes

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import dev.sebaubuntu.openobd.app.models.Theme

interface ColorSchemeProvider {
    /**
     * Return whether this platform supports dynamic colors or not.
     * The returned value must stay the same throughout the app lifecycle.
     */
    val supportsDynamicColors: Boolean

    /**
     * Get the appropriate color scheme for the app given the user preferences.
     *
     * @param theme The preferred [Theme]
     * @param dynamicColors Whether to use dynamic colors or not, if available
     */
    @Composable
    fun getColorScheme(
        theme: Theme,
        dynamicColors: Boolean,
    ): ColorScheme

    companion object {
        val DEFAULT = object : ColorSchemeProvider {
            override val supportsDynamicColors = false

            @Composable
            override fun getColorScheme(
                theme: Theme,
                dynamicColors: Boolean,
            ) = when (theme) {
                Theme.SYSTEM -> when (isSystemInDarkTheme()) {
                    true -> darkColorScheme()
                    false -> lightColorScheme()
                }

                Theme.LIGHT -> lightColorScheme()
                Theme.DARK -> darkColorScheme()
            }
        }
    }
}
