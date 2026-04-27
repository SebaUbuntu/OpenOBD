/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.utils

import org.koin.core.annotation.Single

@Single(
    binds = [
        PermissionsManager::class,
    ],
)
class IosPermissionsManager : PermissionsManager by PermissionsManager.Default
