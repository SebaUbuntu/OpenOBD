/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.sebaubuntu.openobd.models.PlatformContext
import dev.sebaubuntu.openobd.ui.LocalPlatformContext
import dev.sebaubuntu.openobd.ui.apps.App
import openobd.composeapp.generated.resources.Res
import openobd.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
    ) {
        CompositionLocalProvider(
            LocalPlatformContext provides PlatformContext.DEFAULT,
        ) {
            App()
        }
    }
}
