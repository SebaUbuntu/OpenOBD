/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.models

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
abstract class NativePlatform : Platform {
    private val kotlinPlatform = kotlin.native.Platform

    abstract fun platformInformation(): Flow<List<String>>

    @OptIn(ExperimentalCoroutinesApi::class)
    final override fun information() = platformInformation().mapLatest {
        buildList {
            addAll(it)

            add("Can access unaligned: ${kotlinPlatform.canAccessUnaligned}")
            add("Little endian: ${kotlinPlatform.isLittleEndian}")
            add("OS family: ${kotlinPlatform.osFamily}")
            add("CPU architecture: ${kotlinPlatform.cpuArchitecture}")
            add("Is debug binary: ${kotlinPlatform.isDebugBinary}")
            add("Program name: ${kotlinPlatform.programName}")
            add("Is memory leak checker active: ${kotlinPlatform.isMemoryLeakCheckerActive}")
            add("Is cleaners leak checker active: ${kotlinPlatform.isCleanersLeakCheckerActive}")
            add("Available processors: ${kotlinPlatform.getAvailableProcessors()}")
        }
    }
}
