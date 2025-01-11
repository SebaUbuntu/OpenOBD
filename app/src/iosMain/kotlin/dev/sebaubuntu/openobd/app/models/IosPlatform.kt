/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.models

import dev.sebaubuntu.openobd.app.utils.LocaleUtils
import dev.sebaubuntu.openobd.core.models.value.Temperature
import kotlinx.coroutines.flow.flowOf
import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.usesMetricSystem
import platform.UIKit.UIDevice

object IosPlatform : NativePlatform() {
    override fun platformInformation() = flowOf(
        listOf(
            "${UIDevice.currentDevice.systemName()} ${UIDevice.currentDevice.systemVersion}",
        )
    )

    override val deviceType = Platform.DeviceType.MOBILE

    override val measurementSystem = when (NSLocale.currentLocale.usesMetricSystem) {
        true -> MeasurementSystem.METRIC
        false -> NSLocale.currentLocale.countryCode?.let(
            LocaleUtils::measurementSystemForCountry
        ) ?: MeasurementSystem.METRIC
    }

    override val defaultTemperatureUnit = NSLocale.currentLocale.countryCode?.let(
        LocaleUtils::temperatureUnitForCountry
    ) ?: Temperature.Unit.CELSIUS
}
