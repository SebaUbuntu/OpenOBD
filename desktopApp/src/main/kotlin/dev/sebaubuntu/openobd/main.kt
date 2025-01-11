/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.sebaubuntu.openobd.app.di.initKoin
import dev.sebaubuntu.openobd.app.ui.apps.App
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

fun main() {
    initKoin()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = stringResource(Res.string.app_name),
        ) {
            App()
        }
    }
}
