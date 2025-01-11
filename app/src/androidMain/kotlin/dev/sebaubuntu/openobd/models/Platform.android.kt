/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

import android.os.Build

actual object Platform {
    actual val information: List<String> = listOf(
        "Android ${Build.VERSION.SDK_INT}"
    )

    actual val isDesktop: Boolean = false
}
