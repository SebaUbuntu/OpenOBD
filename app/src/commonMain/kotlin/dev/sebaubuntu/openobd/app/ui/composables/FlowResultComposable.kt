/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.error_already_exists
import openobd.app.generated.resources.error_authentication_required
import openobd.app.generated.resources.error_cancelled
import openobd.app.generated.resources.error_deserialization
import openobd.app.generated.resources.error_invalid_credentials
import openobd.app.generated.resources.error_invalid_response
import openobd.app.generated.resources.error_io
import openobd.app.generated.resources.error_not_found
import openobd.app.generated.resources.error_not_implemented
import org.jetbrains.compose.resources.stringResource

@Composable
fun <T> FlowResultComposable(
    flowResult: FlowResult<T, Error>,
    paddingValues: PaddingValues = PaddingValues(),
    successContent: @Composable (T) -> Unit,
) {
    when (flowResult) {
        is FlowResult.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        is FlowResult.Success -> {
            successContent(flowResult.data)
        }

        is FlowResult.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(
                        when (flowResult.error) {
                            Error.NOT_IMPLEMENTED -> Res.string.error_not_implemented
                            Error.IO -> Res.string.error_io
                            Error.AUTHENTICATION_REQUIRED ->
                                Res.string.error_authentication_required

                            Error.INVALID_CREDENTIALS -> Res.string.error_invalid_credentials
                            Error.NOT_FOUND -> Res.string.error_not_found
                            Error.ALREADY_EXISTS -> Res.string.error_already_exists
                            Error.DESERIALIZATION -> Res.string.error_deserialization
                            Error.CANCELLED -> Res.string.error_cancelled
                            Error.INVALID_RESPONSE -> Res.string.error_invalid_response
                        }
                    ),
                )
            }
        }
    }
}
