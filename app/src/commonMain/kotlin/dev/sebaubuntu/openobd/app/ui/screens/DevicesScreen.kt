/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.screens

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
import dev.sebaubuntu.openobd.app.ext.drawableResource
import dev.sebaubuntu.openobd.app.ext.stringResource
import dev.sebaubuntu.openobd.backend.models.DeviceType
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun DevicesScreen(
    paddingValues: PaddingValues,
    onConnectionTypeSelected: (DeviceType) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.padding(paddingValues),
    ) {
        items(DeviceType.entries) {
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
