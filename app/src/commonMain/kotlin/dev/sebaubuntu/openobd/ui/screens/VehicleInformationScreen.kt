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
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sebaubuntu.openobd.obd2.models.ObdResponse
import dev.sebaubuntu.openobd.obd2.models.VehicleInformationType
import dev.sebaubuntu.openobd.repositories.ConnectionStatusRepository
import dev.sebaubuntu.openobd.repositories.ObdRepository
import dev.sebaubuntu.openobd.ui.composables.ConnectionGatedComposable
import dev.sebaubuntu.openobd.ui.composables.FlowResultComposable
import dev.sebaubuntu.openobd.viewmodels.VehicleInformationViewModel

@Composable
fun VehicleInformationScreen(
    paddingValues: PaddingValues,
    connectionStatusRepository: ConnectionStatusRepository,
    obdRepository: ObdRepository,
) {
    ConnectionGatedComposable(
        connectionStatusRepository = connectionStatusRepository,
        paddingValues = paddingValues,
    ) {
        val vehicleInformationViewModel = viewModel {
            VehicleInformationViewModel(
                obdRepository = obdRepository,
            )
        }

        val dataToValue by vehicleInformationViewModel.dataToValue.collectAsStateWithLifecycle()

        FlowResultComposable(
            flowResult = dataToValue,
            paddingValues = paddingValues,
        ) {
            VehicleInformationScreen(
                paddingValues = paddingValues,
                dataToValue = it,
            )
        }
    }
}

@Composable
private fun VehicleInformationScreen(
    paddingValues: PaddingValues,
    dataToValue: List<Pair<VehicleInformationType<*>, ObdResponse<*>>>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues),
    ) {
        items(items = dataToValue) {
            VehicleInformationListItem(
                dataToValue = it,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun VehicleInformationListItem(
    dataToValue: Pair<VehicleInformationType<*>, ObdResponse<*>>,
    modifier: Modifier = Modifier,
) {
    val hexFormat = remember {
        HexFormat {
            number {
                prefix = "0x"
            }
            upperCase = true
        }
    }

    ListItem(
        headlineContent = {
            Text(
                text = dataToValue.first.toString(),
            )
        },
        modifier = modifier,
        overlineContent = {
            Text(
                text = dataToValue.first.parameterId.toHexString(format = hexFormat),
            )
        },
        supportingContent = {
            Text(
                text = dataToValue.second.value.entries.joinToString("\n") {
                    it.toString()
                },
            )
        }
    )
}
