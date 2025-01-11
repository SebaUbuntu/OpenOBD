/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

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
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sebaubuntu.openobd.repositories.ObdRepository
import dev.sebaubuntu.openobd.ui.composables.ConnectionGatedComposable
import dev.sebaubuntu.openobd.ui.composables.FlowResultComposable
import dev.sebaubuntu.openobd.viewmodels.DiagnosticTroubleCodesViewModel

/**
 * DTC error codes screen.
 */
@Composable
fun DiagnosticTroubleCodesScreen(
    paddingValues: PaddingValues,
    obdRepository: ObdRepository,
) {
    ConnectionGatedComposable(
        obdRepository = obdRepository,
        paddingValues = paddingValues,
    ) {
        val diagnosticTroubleCodesViewModel = viewModel {
            DiagnosticTroubleCodesViewModel(
                obdRepository = obdRepository,
            )
        }

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
                text = codeWithControlModules.code.toString(),
            )
        },
        modifier = modifier,
        overlineContent = {
            Text(
                text = codeWithControlModules.code.toString(),
            )
        },
        supportingContent = {
            Text(
                text = codeWithControlModules.controlModules.joinToString()
            )
        }
    )
}
