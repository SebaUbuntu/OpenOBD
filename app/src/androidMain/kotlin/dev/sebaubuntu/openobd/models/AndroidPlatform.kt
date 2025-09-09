/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

import android.app.UiModeManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.core.content.getSystemService
import kotlinx.coroutines.flow.flowOf

class AndroidPlatform(private val context: Context) : Platform {
    private val uiModeManager = context.getSystemService<UiModeManager>()!!

    private val isTv by lazy {
        context.packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }

    override fun information() = flowOf(
        buildList {
            add("Android SDK: ${Build.VERSION.SDK_INT}")
            add("Device codename: ${Build.DEVICE}")
            add("Build fingerprint: ${Build.FINGERPRINT}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add("Hardware SKU: ${Build.SKU}")
                add("ODM SKU: ${Build.ODM_SKU}")
            }
            add("Available processors: ${Runtime.getRuntime().availableProcessors()}")
        }
    )

    override val deviceType = when (uiModeManager.currentModeType) {
        Configuration.UI_MODE_TYPE_UNDEFINED -> isTvOrMobile()
        Configuration.UI_MODE_TYPE_NORMAL -> isTvOrMobile()
        Configuration.UI_MODE_TYPE_DESK -> Platform.DeviceType.DESKTOP
        Configuration.UI_MODE_TYPE_CAR -> Platform.DeviceType.CAR
        Configuration.UI_MODE_TYPE_TELEVISION -> Platform.DeviceType.TV
        Configuration.UI_MODE_TYPE_APPLIANCE -> Platform.DeviceType.DESKTOP
        Configuration.UI_MODE_TYPE_WATCH -> Platform.DeviceType.WATCH
        Configuration.UI_MODE_TYPE_VR_HEADSET -> Platform.DeviceType.MOBILE
        else -> isTvOrMobile()
    }

    private fun isTvOrMobile() = when (isTv) {
        true -> Platform.DeviceType.TV
        false -> Platform.DeviceType.MOBILE
    }
}
