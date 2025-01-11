/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.ext

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

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
