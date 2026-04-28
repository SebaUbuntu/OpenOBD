/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.app.ui.composables.ConnectionGatedComposable
import dev.sebaubuntu.openobd.app.viewmodels.ClearDiagnosticTroubleCodesViewModel
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.clear_dtc
import openobd.app.generated.resources.clear_dtc_clearing
import openobd.app.generated.resources.clear_dtc_failure
import openobd.app.generated.resources.clear_dtc_prompt
import openobd.app.generated.resources.clear_dtc_retry
import openobd.app.generated.resources.clear_dtc_success
import openobd.app.generated.resources.ic_delete
import openobd.app.generated.resources.ic_restart_alt
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ClearDiagnosticTroubleCodesScreen(
    paddingValues: PaddingValues,
) {
    ConnectionGatedComposable(
        paddingValues = paddingValues,
    ) {
        val viewModel = koinViewModel<ClearDiagnosticTroubleCodesViewModel>()

        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        ClearDiagnosticTroubleCodesScreen(
            paddingValues = paddingValues,
            uiState = uiState,
            onClearCodes = viewModel::clearCodes,
        )
    }
}

@Composable
private fun ClearDiagnosticTroubleCodesScreen(
    paddingValues: PaddingValues,
    uiState: ClearDiagnosticTroubleCodesViewModel.UiState,
    onClearCodes: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        when (uiState) {
            ClearDiagnosticTroubleCodesViewModel.UiState.Idle -> {
                Text(
                    text = stringResource(Res.string.clear_dtc_prompt),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                )

                FilledTonalButton(
                    onClick = onClearCodes,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_delete),
                        contentDescription = stringResource(Res.string.clear_dtc),
                        modifier = Modifier.padding(end = 8.dp),
                    )

                    Text(
                        text = stringResource(Res.string.clear_dtc),
                    )
                }
            }

            ClearDiagnosticTroubleCodesViewModel.UiState.Clearing -> {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = stringResource(Res.string.clear_dtc_clearing),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                )
            }

            ClearDiagnosticTroubleCodesViewModel.UiState.Success -> {
                Text(
                    text = stringResource(Res.string.clear_dtc_success),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                )
            }

            ClearDiagnosticTroubleCodesViewModel.UiState.Failure -> {
                Text(
                    text = stringResource(Res.string.clear_dtc_failure),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                )

                FilledTonalButton(
                    onClick = onClearCodes,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_restart_alt),
                        contentDescription = stringResource(Res.string.clear_dtc_retry),
                        modifier = Modifier.padding(end = 8.dp),
                    )

                    Text(
                        text = stringResource(Res.string.clear_dtc_retry),
                    )
                }
            }
        }
    }
}
