/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ext

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun Context.permissionGranted(
    permission: String
) = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.permissionsGranted(permissions: Array<String>) = permissions.all {
    permissionGranted(it)
}

fun Context.permissionsGrantedFlow(
    lifecycle: Lifecycle,
    permissions: Array<String>,
) = lifecycle.eventFlow(Lifecycle.Event.ON_RESUME)
    .onStart { emit(Unit) }
    .map { permissionsGranted(permissions) }

/**
 * [BroadcastReceiver] as flow.
 *
 * @param intentFilter The [IntentFilter] used to match intents.
 * @param flags See [ContextCompat.registerReceiver]
 * @param resolve See [IntentFilter.match]
 * @param logTag Optional log tag for logging
 */
fun Context.broadcastReceiverFlow(
    intentFilter: IntentFilter,
    @ContextCompat.RegisterReceiverFlags flags: Int,
    resolve: Boolean,
    logTag: String? = null,
) = callbackFlow {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.also {
                if (intentFilter.match(contentResolver, it, resolve, logTag) >= 0) {
                    trySend(it)
                }
            }
        }
    }

    ContextCompat.registerReceiver(
        this@broadcastReceiverFlow,
        receiver,
        intentFilter,
        flags,
    )

    awaitClose {
        unregisterReceiver(receiver)
    }
}
