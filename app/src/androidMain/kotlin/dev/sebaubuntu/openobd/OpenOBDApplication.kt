/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd

import android.app.Application
import dev.sebaubuntu.openobd.di.initKoin
import org.koin.android.ext.koin.androidContext

class OpenOBDApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@OpenOBDApplication)
        }
    }
}
