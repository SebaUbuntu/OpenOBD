/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

import openobd.composeapp.generated.resources.Res
import openobd.composeapp.generated.resources.permission_bluetooth
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
