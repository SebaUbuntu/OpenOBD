/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

actual abstract class PlatformContext {
    companion object {
        val DEFAULT = object : PlatformContext() {}
    }
}
