/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.sebaubuntu.openobd.app.ui.apps.App
import dev.sebaubuntu.openobd.app.utils.PermissionsManager
import org.koin.android.ext.android.get
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.scope.Scope
import org.koin.dsl.module

class MainActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope: Scope by activityScope()

    private val permissionsManager: PermissionsManager = get()

    /**
     * Cringe: We need to inject the activity scope to the global one manually.
     */
    private val activityModule = module {
        single { permissionsManager }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadKoinModules(activityModule)

        // Enable edge-to-edge
        enableEdgeToEdge()

        setContent {
            App()
        }
    }

    override fun onCloseScope() {
        unloadKoinModules(activityModule)

        super.onCloseScope()
    }
}
