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
import dev.sebaubuntu.openobd.obd.models.Data
import dev.sebaubuntu.openobd.repositories.ObdRepository
import dev.sebaubuntu.openobd.ui.composables.ConnectionGatedComposable
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

        CurrentDataScreen(
            paddingValues = paddingValues,
            dataToValue = dataToValue,
        )
    }
}

@Composable
private fun CurrentDataScreen(
    paddingValues: PaddingValues,
    dataToValue: List<Pair<Data<*>, Any?>>,
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

@OptIn(ExperimentalStdlibApi::class)
@Composable
private fun DataListItem(
    dataToValue: Pair<Data<*>, Any?>,
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
                text = dataToValue.first.pid.toHexString(format = hexFormat),
            )
        },
        supportingContent = {
            Text(
                text = dataToValue.second.toString(),
            )
        }
    )
}
