/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.models.Platform
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.device_type
import openobd.app.generated.resources.device_type_car
import openobd.app.generated.resources.device_type_desktop
import openobd.app.generated.resources.device_type_mobile
import openobd.app.generated.resources.device_type_tv
import openobd.app.generated.resources.device_type_watch
import openobd.app.generated.resources.platform_information
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * App preferences screen.
 */
@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
) {
    val platform = koinInject<Platform>()

    val platformInformation by platform.information().collectAsStateWithLifecycle(listOf())
    val deviceType = platform.deviceType

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
    ) {
        SettingListItem(
            titleStringResource = Res.string.platform_information,
            description = platformInformation.joinToString("\n"),
        )

        SettingListItem(
            titleStringResource = Res.string.device_type,
            description = stringResource(
                when (deviceType) {
                    Platform.DeviceType.DESKTOP -> Res.string.device_type_desktop
                    Platform.DeviceType.MOBILE -> Res.string.device_type_mobile
                    Platform.DeviceType.TV -> Res.string.device_type_tv
                    Platform.DeviceType.WATCH -> Res.string.device_type_watch
                    Platform.DeviceType.CAR -> Res.string.device_type_car
                }
            ),
        )
    }
}

@Composable
fun SettingListItem(
    titleStringResource: StringResource,
    description: String,
    modifier: Modifier = Modifier,
) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(titleStringResource),
            )
        },
        modifier = modifier,
        supportingContent = {
            Text(
                text = description,
            )
        }
    )
}
