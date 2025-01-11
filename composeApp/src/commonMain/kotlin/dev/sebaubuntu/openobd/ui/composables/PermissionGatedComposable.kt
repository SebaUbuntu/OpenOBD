/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.models.Permission
import dev.sebaubuntu.openobd.models.PermissionState
import dev.sebaubuntu.openobd.ui.LocalPermissionsManager
import dev.sebaubuntu.openobd.utils.PermissionsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import openobd.composeapp.generated.resources.Res
import openobd.composeapp.generated.resources.permission_denied
import openobd.composeapp.generated.resources.permission_not_granted
import openobd.composeapp.generated.resources.request_permission
import org.jetbrains.compose.resources.stringResource

@Composable
fun PermissionGatedComposable(
    permission: Permission,
    permissionsManager: PermissionsManager = LocalPermissionsManager.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    val permissionState by permissionsManager.permissionStateFlow(
        permission
    ).collectAsStateWithLifecycle(null)

    when (permissionState) {
        PermissionState.GRANTED -> {
            content()
        }

        PermissionState.NOT_GRANTED -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(
                        Res.string.permission_not_granted,
                        stringResource(permission.stringResource),
                    ),
                )

                Button(
                    onClick = {
                        coroutineScope.launch {
                            permissionsManager.requestPermission(permission)
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.request_permission),
                    )
                }
            }
        }

        PermissionState.DENIED -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(
                        Res.string.permission_denied,
                        stringResource(permission.stringResource),
                    ),
                )
            }
        }

        null -> {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues),
            )
        }
    }
}
