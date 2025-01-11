/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.sebaubuntu.openobd.models.ConnectionType
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DevicesScreen(
    paddingValues: PaddingValues,
    onConnectionTypeSelected: (ConnectionType) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(paddingValues),
    ) {
        items(ConnectionType.entries) {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource(it.stringResource),
                    )
                },
                modifier = Modifier
                    .clickable {
                        onConnectionTypeSelected(it)
                    },
                leadingContent = {
                    Icon(
                        painter = painterResource(it.drawableResource),
                        contentDescription = stringResource(it.stringResource),
                    )
                },
            )
        }
    }
}
