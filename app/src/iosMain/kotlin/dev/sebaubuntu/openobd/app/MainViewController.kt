/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app

import androidx.compose.ui.window.ComposeUIViewController
import dev.sebaubuntu.openobd.app.ui.apps.App

fun MainViewController() = ComposeUIViewController { App() }
