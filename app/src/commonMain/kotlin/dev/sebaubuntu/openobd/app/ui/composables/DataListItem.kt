/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.composables

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.sebaubuntu.openobd.elm327.models.CanResponse
import dev.sebaubuntu.openobd.obd2.models.DataType

@Composable
fun DataListItem(
    dataToValue: Pair<DataType<*>, CanResponse<*>>,
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
