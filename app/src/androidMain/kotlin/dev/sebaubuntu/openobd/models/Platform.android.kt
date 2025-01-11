/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

import android.os.Build

actual object Platform {
    actual val information: List<String> = buildList {
        add("Android SDK: ${Build.VERSION.SDK_INT}")
        add("Device codename: ${Build.DEVICE}")
        add("Build fingerprint: ${Build.FINGERPRINT}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            add("Hardware SKU: ${Build.SKU}")
            add("ODM SKU: ${Build.ODM_SKU}")
        }
    }

    actual val isDesktop: Boolean = false
}
