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
import dev.sebaubuntu.openobd.app.models.Permission
import dev.sebaubuntu.openobd.app.models.PermissionState
import dev.sebaubuntu.openobd.app.utils.PermissionsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.permission_denied
import openobd.app.generated.resources.permission_not_granted
import openobd.app.generated.resources.request_permission
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun PermissionGatedComposable(
    permission: Permission,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    paddingValues: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    val permissionsManager = koinInject<PermissionsManager>()

    val permissionState by permissionsManager.permissionStateFlow(
        permission
    ).collectAsStateWithLifecycle(null)

    PermissionGatedComposable(
        permission = permission,
        permissionState = permissionState,
        paddingValues = paddingValues,
        onRequestPermission = {
            coroutineScope.launch(Dispatchers.IO) {
                permissionsManager.requestPermission(permission)
            }
        },
        content = content,
    )
}

@Composable
private fun PermissionGatedComposable(
    permission: Permission,
    permissionState: PermissionState?,
    paddingValues: PaddingValues = PaddingValues(),
    onRequestPermission: () -> Unit,
    content: @Composable () -> Unit,
) {
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
                        onRequestPermission()
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
