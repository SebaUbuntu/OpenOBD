/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd

import androidx.compose.ui.window.ComposeUIViewController
import dev.sebaubuntu.openobd.ui.apps.App

fun MainViewController() = ComposeUIViewController { App() }
