/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.app.ui.composables.ConnectionGatedComposable
import dev.sebaubuntu.openobd.app.ui.composables.FlowResultComposable
import dev.sebaubuntu.openobd.app.viewmodels.DiagnosticTroubleCodesViewModel
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.unknown_dtc
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * DTC error codes screen.
 */
@Composable
fun DiagnosticTroubleCodesScreen(
    paddingValues: PaddingValues,
) {
    ConnectionGatedComposable(
        paddingValues = paddingValues,
    ) {
        val diagnosticTroubleCodesViewModel = koinViewModel<DiagnosticTroubleCodesViewModel>()

        val codesWithControlModules by diagnosticTroubleCodesViewModel.codesWithControlModules.collectAsStateWithLifecycle()

        FlowResultComposable(
            flowResult = codesWithControlModules,
            paddingValues = paddingValues,
        ) {
            DiagnosticTroubleCodesScreen(
                paddingValues = paddingValues,
                codesWithControlModules = it,
            )
        }
    }
}

@Composable
private fun DiagnosticTroubleCodesScreen(
    paddingValues: PaddingValues,
    codesWithControlModules: List<DiagnosticTroubleCodesViewModel.CodeWithControlModules>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues = paddingValues),
    ) {
        items(codesWithControlModules) {
            CodeWithControlModulesListItem(
                codeWithControlModules = it,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CodeWithControlModulesListItem(
    codeWithControlModules: DiagnosticTroubleCodesViewModel.CodeWithControlModules,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text = codeWithControlModules.information?.name ?: stringResource(
                    Res.string.unknown_dtc
                ),
            )
        },
        modifier = modifier,
        supportingContent = {
            Text(
                text = buildString {
                    codeWithControlModules.information?.description?.let {
                        appendLine(it)
                        appendLine()
                    }

                    append(
                        codeWithControlModules.controlModulesWithStatus.joinToString("\n") {
                            "${it.controlModule}: ${it.status.joinToString()}"
                        }
                    )
                }
            )
        },
        trailingContent = {
            Text(
                text = codeWithControlModules.code.toString(),
            )
        },
    )
}
