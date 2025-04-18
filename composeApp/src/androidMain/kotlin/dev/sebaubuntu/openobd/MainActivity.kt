/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import dev.sebaubuntu.openobd.ui.LocalBluetoothManager
import dev.sebaubuntu.openobd.ui.LocalPermissionsManager
import dev.sebaubuntu.openobd.ui.LocalPlatformContext
import dev.sebaubuntu.openobd.ui.apps.App
import dev.sebaubuntu.openobd.utils.AndroidBluetoothManager
import dev.sebaubuntu.openobd.utils.AndroidPermissionsManager

class MainActivity : ComponentActivity() {
    // Permissions manager
    private val permissionsManager = AndroidPermissionsManager(this)

    // Bluetooth manager
    private val bluetoothManager by lazy {
        AndroidBluetoothManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable edge-to-edge
        enableEdgeToEdge()

        setContent {
            CompositionLocalProvider(
                LocalPlatformContext provides LocalContext.current,
                LocalPermissionsManager provides permissionsManager,
                LocalBluetoothManager provides bluetoothManager,
            ) {
                App()
            }
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    CompositionLocalProvider(
        LocalPlatformContext provides LocalContext.current,
    ) {
        App()
    }
}
