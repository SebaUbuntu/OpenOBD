/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.themes

import org.koin.core.annotation.Single

@Single(
    binds = [
        ColorSchemeProvider::class,
    ],
)
class JvmColorSchemeProvider : ColorSchemeProvider by ColorSchemeProvider.Default
