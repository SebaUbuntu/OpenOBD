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
import dev.sebaubuntu.openobd.obd2.models.DataType
import dev.sebaubuntu.openobd.obd2.models.ObdResponse
import dev.sebaubuntu.openobd.repositories.ConnectionStatusRepository
import dev.sebaubuntu.openobd.repositories.ObdRepository
import dev.sebaubuntu.openobd.ui.composables.ConnectionGatedComposable
import dev.sebaubuntu.openobd.ui.composables.DataListItem
import dev.sebaubuntu.openobd.ui.composables.FlowResultComposable
import dev.sebaubuntu.openobd.viewmodels.FreezeFrameDataViewModel

/**
 * List of all sensors screen.
 */
@Composable
fun FreezeFrameDataScreen(
    paddingValues: PaddingValues,
    connectionStatusRepository: ConnectionStatusRepository,
    obdRepository: ObdRepository,
) {
    ConnectionGatedComposable(
        connectionStatusRepository = connectionStatusRepository,
        paddingValues = paddingValues,
    ) {
        val freezeFrameDataViewModel = viewModel {
            FreezeFrameDataViewModel(
                obdRepository = obdRepository,
            )
        }

        val dataToValue by freezeFrameDataViewModel.dataToValue.collectAsStateWithLifecycle()

        FlowResultComposable(
            flowResult = dataToValue,
            paddingValues = paddingValues,
        ) {
            FreezeFrameDataScreen(
                paddingValues = paddingValues,
                dataToValue = it,
            )
        }
    }
}

@Composable
private fun FreezeFrameDataScreen(
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
