/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.composables

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
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sebaubuntu.openobd.repositories.ConnectionStatusRepository
import dev.sebaubuntu.openobd.viewmodels.ConnectionGatedViewModel
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.no_active_connection
import org.jetbrains.compose.resources.stringResource

/**
 * A composable that requires an active OBD connection.
 *
 * @param content The composable to render if the connection is active
 */
@Composable
fun ConnectionGatedComposable(
    connectionStatusRepository: ConnectionStatusRepository,
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    val connectionGatedViewModel = viewModel {
        ConnectionGatedViewModel(
            connectionStatusRepository = connectionStatusRepository,
        )
    }

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
