/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.utils

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import dev.sebaubuntu.openobd.app.ext.permissionsGranted
import kotlinx.coroutines.channels.Channel

/**
 * A coroutine-based class that checks main app permissions.
 */
class PermissionsChecker(
    private val activity: ComponentActivity,
    private val permissions: Array<String>,
) {
    private val channel = Channel<Boolean>(1)

    private val activityResultLauncher = activity.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        if (it.isNotEmpty()) {
            channel.trySend(activity.permissionsGranted(permissions))
        }
    }

    suspend fun requestPermissions() = when (activity.permissionsGranted(permissions)) {
        true -> true
        false -> {
            activityResultLauncher.launch(permissions)
            channel.receive()
        }
    }
}
