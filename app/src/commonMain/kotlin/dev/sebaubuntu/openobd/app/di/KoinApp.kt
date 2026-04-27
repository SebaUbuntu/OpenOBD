/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.di

import dev.sebaubuntu.openobd.core.di.CoreModule
import dev.sebaubuntu.openobd.logging.di.LoggingModule
import dev.sebaubuntu.openobd.network.devices.di.NetworkDevicesModule
import dev.sebaubuntu.openobd.network.elm327.di.NetworkElm327Module
import dev.sebaubuntu.openobd.storage.di.StorageModule
import org.koin.core.annotation.KoinApplication

@KoinApplication(
    modules = [
        AppModule::class,
        CoreModule::class,
        LoggingModule::class,
        NetworkDevicesModule::class,
        NetworkElm327Module::class,
        StorageModule::class,
    ],
)
class KoinApp
