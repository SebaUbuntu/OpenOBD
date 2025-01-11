/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

import kotlinx.coroutines.flow.flowOf
import platform.UIKit.UIDevice

object IosPlatform : NativePlatform() {
    override fun platformInformation() = flowOf(
        listOf(
            "${UIDevice.currentDevice.systemName()} ${UIDevice.currentDevice.systemVersion}",
        )
    )

    override val deviceType = Platform.DeviceType.MOBILE
}
