/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.models

import openobd.app.generated.resources.Res
import openobd.app.generated.resources.permission_bluetooth
import org.jetbrains.compose.resources.StringResource

/**
 * Permissions.
 */
enum class Permission(
    val stringResource: StringResource,
) {
    /**
     * Permissions for Bluetooth features.
     */
    BLUETOOTH(
        stringResource = Res.string.permission_bluetooth,
    ),
}
