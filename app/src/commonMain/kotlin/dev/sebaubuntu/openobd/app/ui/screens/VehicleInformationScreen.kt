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
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.app.ui.composables.ConnectionGatedComposable
import dev.sebaubuntu.openobd.app.ui.composables.FlowResultComposable
import dev.sebaubuntu.openobd.app.viewmodels.VehicleInformationViewModel
import dev.sebaubuntu.openobd.elm327.models.CanResponse
import dev.sebaubuntu.openobd.obd2.models.VehicleInformationType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun VehicleInformationScreen(
    paddingValues: PaddingValues,
) {
    ConnectionGatedComposable(
        paddingValues = paddingValues,
    ) {
        val vehicleInformationViewModel = koinViewModel<VehicleInformationViewModel>()

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
    dataToValue: List<Pair<VehicleInformationType<*>, CanResponse<*>>>,
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
    dataToValue: Pair<VehicleInformationType<*>, CanResponse<*>>,
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
