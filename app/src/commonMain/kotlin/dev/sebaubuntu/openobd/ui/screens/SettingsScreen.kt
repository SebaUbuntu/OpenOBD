/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.sebaubuntu.openobd.models.Platform
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.platform_name
import org.jetbrains.compose.resources.stringResource

/**
 * App preferences screen.
 */
@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(Res.string.platform_name),
                )
            },
            supportingContent = {
                Text(
                    text = Platform.information.joinToString("\n"),
                )
            }
        )
    }
}
