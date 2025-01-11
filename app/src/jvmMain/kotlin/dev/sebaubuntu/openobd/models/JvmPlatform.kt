/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

import kotlinx.coroutines.flow.flowOf

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
}
