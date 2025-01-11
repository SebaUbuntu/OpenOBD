/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

actual object Platform {
    actual val name: String = "Java ${System.getProperty("java.version")}"
}
