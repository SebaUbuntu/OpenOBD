/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

import platform.UIKit.UIDevice

actual object Platform {
    actual val information: List<String> = listOf(
        "${UIDevice.currentDevice.systemName()} ${UIDevice.currentDevice.systemVersion}"
    )

    actual val isDesktop: Boolean = false
}
