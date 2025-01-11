/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.composables

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.sebaubuntu.openobd.obd.models.DataType
import dev.sebaubuntu.openobd.obd.models.ObdResponse

@OptIn(ExperimentalStdlibApi::class)
@Composable
fun DataListItem(
    dataToValue: Pair<DataType<*>, ObdResponse<*>>,
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
