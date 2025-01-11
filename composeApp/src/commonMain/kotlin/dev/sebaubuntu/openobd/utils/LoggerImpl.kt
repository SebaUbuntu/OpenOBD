/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.utils

expect object LoggerImpl {
    inline fun log(
        level: Logger.Level,
        tag: String?,
        throwable: Throwable?,
        message: () -> String,
    )
}
