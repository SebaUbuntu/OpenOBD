/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.app.viewmodels.LogsViewModel
import dev.sebaubuntu.openobd.logging.LogEntry
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.ExperimentalTime

@Composable
fun LogsScreen(
    paddingValues: PaddingValues,
) {
    val logsViewModel = koinViewModel<LogsViewModel>()

    val logEntries by logsViewModel.logEntries.collectAsStateWithLifecycle()

    LogsScreen(
        paddingValues = paddingValues,
        logEntries = logEntries,
    )
}

@Composable
private fun LogsScreen(
    paddingValues: PaddingValues,
    logEntries: List<LogEntry>,
) {
    val lazyListState = rememberLazyListState()

    LaunchedEffect(logEntries.size) {
        lazyListState.animateScrollToItem(lazyListState.layoutInfo.totalItemsCount)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues),
        state = lazyListState,
        verticalArrangement = Arrangement.Bottom,
    ) {
        itemsIndexed(logEntries) { index, item ->
            LogEntryListItem(
                logEntry = item,
            )

            if (index < logEntries.lastIndex) {
                HorizontalDivider()
            }
        }
    }
}

@Composable
@OptIn(ExperimentalTime::class)
private fun LogEntryListItem(
    logEntry: LogEntry,
    modifier: Modifier = Modifier,
) {
    val horizontalScrollState = rememberScrollState()

    ListItem(
        headlineContent = {
            Text(
                text = logEntry.message,
            )
        },
        modifier = modifier,
        overlineContent = {
            Text(
                text = buildString {
                    append(logEntry.level)
                    append(" - ")
                    logEntry.tag?.let {
                        append(it)
                        append(" - ")
                    }
                    append(logEntry.timestamp)
                },
            )
        },
        supportingContent = {
            logEntry.throwable?.let {
                Text(
                    text = it.stackTraceToString().replace("\t", "    "),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(state = horizontalScrollState),
                    fontFamily = FontFamily.Monospace,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 5,
                )
            }
        },
    )
}
