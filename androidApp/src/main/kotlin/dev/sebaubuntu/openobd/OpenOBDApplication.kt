/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd

import android.app.Application
import dev.sebaubuntu.openobd.app.di.initKoin
import org.koin.android.ext.koin.androidContext

class OpenOBDApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@OpenOBDApplication)
        }
    }
}
