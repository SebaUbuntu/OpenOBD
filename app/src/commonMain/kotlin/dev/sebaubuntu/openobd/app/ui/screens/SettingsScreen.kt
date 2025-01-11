/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.app.models.Theme
import dev.sebaubuntu.openobd.app.ui.composables.EnumPreferenceListItem
import dev.sebaubuntu.openobd.app.ui.composables.InformationListItem
import dev.sebaubuntu.openobd.app.ui.composables.PreferencesCategoryListItem
import dev.sebaubuntu.openobd.app.ui.composables.SwitchPreferenceListItem
import dev.sebaubuntu.openobd.app.ui.themes.ColorSchemeProvider
import dev.sebaubuntu.openobd.app.viewmodels.SettingsViewModel
import dev.sebaubuntu.openobd.core.models.value.Speed
import dev.sebaubuntu.openobd.core.models.value.Temperature
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.dynamic_colors
import openobd.app.generated.resources.dynamic_colors_description
import openobd.app.generated.resources.platform_information
import openobd.app.generated.resources.settings_general
import openobd.app.generated.resources.settings_platform
import openobd.app.generated.resources.settings_units
import openobd.app.generated.resources.speed_unit
import openobd.app.generated.resources.speed_unit_kilometer_per_hour
import openobd.app.generated.resources.speed_unit_meter_per_second
import openobd.app.generated.resources.speed_unit_mile_per_hour
import openobd.app.generated.resources.temperature_unit
import openobd.app.generated.resources.temperature_unit_celsius
import openobd.app.generated.resources.temperature_unit_fahrenheit
import openobd.app.generated.resources.temperature_unit_kelvin
import openobd.app.generated.resources.theme
import openobd.app.generated.resources.theme_dark
import openobd.app.generated.resources.theme_light
import openobd.app.generated.resources.theme_system
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * App settings screen.
 */
@Composable
fun SettingsScreen(
    paddingValues: PaddingValues,
) {
    val settingsViewModel = koinViewModel<SettingsViewModel>()
    val colorSchemeProvider = koinInject<ColorSchemeProvider>()

    val supportsDynamicColors = remember {
        colorSchemeProvider.supportsDynamicColors
    }
    val platformInformation by settingsViewModel.platformInformation.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState()),
    ) {
        // General
        PreferencesCategoryListItem(
            titleStringResource = Res.string.settings_general,
        )

        EnumPreferenceListItem(
            preferenceHolder = settingsViewModel.theme,
            onPreferenceChange = settingsViewModel::setPreferenceValue,
            titleStringResource = Res.string.theme,
            valueToDescriptionStringResource = {
                when (it) {
                    Theme.LIGHT -> Res.string.theme_light
                    Theme.DARK -> Res.string.theme_dark
                    Theme.SYSTEM -> Res.string.theme_system
                }
            }
        )

        if (supportsDynamicColors) {
            SwitchPreferenceListItem(
                preferenceHolder = settingsViewModel.dynamicColors,
                onPreferenceChange = settingsViewModel::setPreferenceValue,
                titleStringResource = Res.string.dynamic_colors,
                descriptionStringResource = Res.string.dynamic_colors_description,
            )
        }

        // Units
        PreferencesCategoryListItem(
            titleStringResource = Res.string.settings_units,
        )

        EnumPreferenceListItem(
            preferenceHolder = settingsViewModel.speedUnit,
            onPreferenceChange = settingsViewModel::setPreferenceValue,
            titleStringResource = Res.string.speed_unit,
            valueToDescriptionStringResource = {
                when (it) {
                    Speed.Unit.METER_PER_SECOND -> Res.string.speed_unit_meter_per_second
                    Speed.Unit.MILE_PER_HOUR -> Res.string.speed_unit_mile_per_hour
                    Speed.Unit.KILOMETER_PER_HOUR -> Res.string.speed_unit_kilometer_per_hour
                }
            }
        )

        EnumPreferenceListItem(
            preferenceHolder = settingsViewModel.temperatureUnit,
            onPreferenceChange = settingsViewModel::setPreferenceValue,
            titleStringResource = Res.string.temperature_unit,
            valueToDescriptionStringResource = {
                when (it) {
                    Temperature.Unit.CELSIUS -> Res.string.temperature_unit_celsius
                    Temperature.Unit.FAHRENHEIT -> Res.string.temperature_unit_fahrenheit
                    Temperature.Unit.KELVIN -> Res.string.temperature_unit_kelvin
                }
            }
        )

        // Platform
        PreferencesCategoryListItem(
            titleStringResource = Res.string.settings_platform,
        )

        platformInformation?.let {
            InformationListItem(
                titleStringResource = Res.string.platform_information,
                description = it.joinToString("\n"),
            )
        }
    }
}
