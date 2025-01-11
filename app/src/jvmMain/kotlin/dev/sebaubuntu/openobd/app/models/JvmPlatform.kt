/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.models

import dev.sebaubuntu.openobd.app.utils.LocaleUtils
import kotlinx.coroutines.flow.flowOf
import java.util.Locale

object JvmPlatform : Platform {
    override fun information() = flowOf(
        listOf(
            "Java version: ${System.getProperty("java.version")}",
            "JVM vendor: ${System.getProperty("java.vm.vendor")}",
            "JVM version: ${System.getProperty("java.vm.version")}",
            "OS name: ${System.getProperty("os.name")}",
            "OS version: ${System.getProperty("os.version")}",
            "OS architecture: ${System.getProperty("os.arch")}",
            "Available processors: ${Runtime.getRuntime().availableProcessors()}",
        )
    )

    override val deviceType = Platform.DeviceType.DESKTOP

    override val measurementSystem
        get() = LocaleUtils.measurementSystemForCountry(Locale.getDefault().country)

    override val defaultTemperatureUnit
        get() = LocaleUtils.temperatureUnitForCountry(Locale.getDefault().country)
}
