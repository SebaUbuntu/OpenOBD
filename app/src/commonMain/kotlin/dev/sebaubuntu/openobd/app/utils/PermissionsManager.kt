/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.utils

import dev.sebaubuntu.openobd.app.models.Permission
import dev.sebaubuntu.openobd.app.models.PermissionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Generic interface for managing permissions.
 */
interface PermissionsManager {
    /**
     * Check the state of the requested permission.
     */
    suspend fun permissionState(permission: Permission): PermissionState

    /**
     * Observe the state of the requested permission.
     */
    fun permissionStateFlow(permission: Permission): Flow<PermissionState>

    /**
     * Request permission for the requested permission (lol).
     *
     * @param permission The permission to request
     * @return The new state of the permission
     */
    suspend fun requestPermission(permission: Permission): PermissionState

    companion object {
        /**
         * Default implementation of [PermissionsManager]. Will assume that all permissions are
         * granted.
         */
        val DEFAULT = object : PermissionsManager {
            override suspend fun permissionState(permission: Permission) = PermissionState.GRANTED

            override fun permissionStateFlow(
                permission: Permission
            ) = flowOf(PermissionState.GRANTED)

            override suspend fun requestPermission(permission: Permission) = PermissionState.GRANTED
        }
    }
}
