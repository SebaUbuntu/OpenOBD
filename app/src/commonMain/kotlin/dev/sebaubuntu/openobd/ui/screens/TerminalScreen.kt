/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.repositories.ObdRepository
import dev.sebaubuntu.openobd.ui.composables.ConnectionGatedComposable
import dev.sebaubuntu.openobd.viewmodels.TerminalViewModel
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.send_command
import org.jetbrains.compose.resources.stringResource

/**
 * ELM327 console screen.
 */
@Composable
fun TerminalScreen(
    paddingValues: PaddingValues,
    deviceConnectionRepository: DeviceConnectionRepository,
    obdRepository: ObdRepository,
) {
    ConnectionGatedComposable(
        deviceConnectionRepository = deviceConnectionRepository,
        paddingValues = paddingValues,
    ) {
        val terminalViewModel = viewModel {
            TerminalViewModel(
                obdRepository = obdRepository,
            )
        }

        val exchanges = terminalViewModel.exchanges.collectAsStateWithLifecycle()

        TerminalScreen(
            paddingValues = paddingValues,
            exchanges = exchanges.value,
            onSendCommand = terminalViewModel::sendCommand,
        )
    }
}

@Composable
private fun TerminalScreen(
    paddingValues: PaddingValues,
    exchanges: List<TerminalViewModel.Exchange>,
    onSendCommand: (String) -> Unit,
) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(exchanges.size) {
        lazyListState.animateScrollToItem(lazyListState.layoutInfo.totalItemsCount)
    }

    var consoleText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues),
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f, fill = true),
            state = lazyListState,
            verticalArrangement = Arrangement.Bottom,
        ) {
            itemsIndexed(exchanges) { index, item ->
                ExchangeListItem(
                    exchange = item,
                )

                if (index < exchanges.lastIndex) {
                    HorizontalDivider()
                }
            }
        }

        Row {
            TextField(
                value = consoleText,
                onValueChange = { consoleText = it },
                modifier = Modifier.weight(1f, fill = true),
                singleLine = true,
            )
            Button(
                onClick = {
                    onSendCommand(consoleText)
                },
            ) {
                Text(
                    text = stringResource(Res.string.send_command),
                )
            }
        }
    }
}

@Composable
private fun ExchangeListItem(
    exchange: TerminalViewModel.Exchange,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text = "> ${exchange.command}",
            )
        },
        modifier = modifier,
        supportingContent = {
            Text(
                text = when (exchange.response) {
                    is Result.Success -> exchange.response.data
                    is Result.Error -> exchange.response.error.toString()
                }
            )
        }
    )
}
