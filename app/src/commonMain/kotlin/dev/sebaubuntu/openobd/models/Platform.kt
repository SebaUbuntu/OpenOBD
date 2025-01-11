/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

/**
 * Platform information.
 */
expect object Platform {
    /**
     * Get a list of non-localized information that can be used to identify the platform.
     */
    val information: List<String>

    /**
     * Whether the platform is a desktop.
     */
    val isDesktop: Boolean
}
