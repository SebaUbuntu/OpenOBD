/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.elm327.di

import dev.sebaubuntu.openobd.core.di.CoreModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Module

@Module(
    includes = [
        CoreModule::class,
    ],
)
@Configuration
@ComponentScan("dev.sebaubuntu.openobd.network.elm327")
class NetworkElm327Module
