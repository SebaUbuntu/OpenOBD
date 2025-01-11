/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.models

/**
 * State of a permission.
 */
enum class PermissionState {
    /**
     * The permission is granted.
     */
    GRANTED,

    /**
     * The permission is not granted and the user hasn't been asked for it.
     */
    NOT_GRANTED,

    /**
     * The user explicitly denied the permission.
     */
    DENIED,
}
