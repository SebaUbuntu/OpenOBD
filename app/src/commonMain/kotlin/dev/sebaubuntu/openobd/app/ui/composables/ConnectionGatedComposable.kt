/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.app.viewmodels.ConnectionGatedViewModel
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.no_active_connection
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * A composable that requires an active OBD connection.
 *
 * @param content The composable to render if the connection is active
 */
@Composable
fun ConnectionGatedComposable(
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    val connectionGatedViewModel = koinViewModel<ConnectionGatedViewModel>()

    val hasActiveConnection by connectionGatedViewModel.hasActiveConnection.collectAsStateWithLifecycle()

    ConnectionGatedComposable(
        hasActiveConnection = hasActiveConnection,
        paddingValues = paddingValues,
        content = content,
    )
}

@Composable
private fun ConnectionGatedComposable(
    hasActiveConnection: Boolean?,
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    when (hasActiveConnection) {
        true -> {
            content()
        }

        false -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(Res.string.no_active_connection),
                )
            }
        }

        null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
