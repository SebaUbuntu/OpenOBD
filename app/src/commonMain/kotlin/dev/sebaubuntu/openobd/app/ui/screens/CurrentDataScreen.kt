/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.screens

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
import dev.sebaubuntu.openobd.app.ui.composables.ConnectionGatedComposable
import dev.sebaubuntu.openobd.app.ui.composables.DataListItem
import dev.sebaubuntu.openobd.app.ui.composables.FlowResultComposable
import dev.sebaubuntu.openobd.app.viewmodels.CurrentDataViewModel
import dev.sebaubuntu.openobd.elm327.models.CanResponse
import dev.sebaubuntu.openobd.obd2.models.DataType
import org.koin.compose.viewmodel.koinViewModel

/**
 * List of all sensors screen.
 */
@Composable
fun CurrentDataScreen(
    paddingValues: PaddingValues,
) {
    ConnectionGatedComposable(
        paddingValues = paddingValues,
    ) {
        val currentDataViewModel = koinViewModel<CurrentDataViewModel>()

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
    dataToValue: List<Pair<DataType<*>, CanResponse<*>>>,
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
