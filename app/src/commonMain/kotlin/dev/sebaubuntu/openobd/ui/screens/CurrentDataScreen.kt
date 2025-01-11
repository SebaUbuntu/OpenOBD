/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sebaubuntu.openobd.obd.models.DataType
import dev.sebaubuntu.openobd.obd.models.ObdResponse
import dev.sebaubuntu.openobd.repositories.ObdRepository
import dev.sebaubuntu.openobd.ui.composables.ConnectionGatedComposable
import dev.sebaubuntu.openobd.ui.composables.DataListItem
import dev.sebaubuntu.openobd.ui.composables.FlowResultComposable
import dev.sebaubuntu.openobd.viewmodels.CurrentDataViewModel

/**
 * List of all sensors screen.
 */
@Composable
fun CurrentDataScreen(
    paddingValues: PaddingValues,
    obdRepository: ObdRepository,
) {
    ConnectionGatedComposable(
        obdRepository = obdRepository,
        paddingValues = paddingValues,
    ) {
        val currentDataViewModel = viewModel {
            CurrentDataViewModel(
                obdRepository = obdRepository,
            )
        }

        val dataToValue by currentDataViewModel.dataToValue.collectAsStateWithLifecycle()

        FlowResultComposable(
            flowResult = dataToValue,
            paddingValues = paddingValues,
        ) {
            CurrentDataScreen(
                paddingValues = paddingValues,
                dataToValue = it,
            )
        }
    }
}

@Composable
private fun CurrentDataScreen(
    paddingValues: PaddingValues,
    dataToValue: List<Pair<DataType<*>, ObdResponse<*>>>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues),
    ) {
        items(items = dataToValue) {
            DataListItem(
                dataToValue = it,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
