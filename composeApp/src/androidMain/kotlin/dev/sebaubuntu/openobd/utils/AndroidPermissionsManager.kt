/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.utils

import android.Manifest
import androidx.activity.ComponentActivity
import dev.sebaubuntu.openobd.ext.permissionsGranted
import dev.sebaubuntu.openobd.ext.permissionsGrantedFlow
import dev.sebaubuntu.openobd.models.Permission
import dev.sebaubuntu.openobd.models.PermissionState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

class AndroidPermissionsManager(
    private val activity: ComponentActivity,
) : PermissionsManager {
    // Each permission needs a PermissionsChecker
    private val permissionsCheckers = Permission.entries.associateWith {
        PermissionsChecker(activity, it.toAndroidPermissions())
    }

    override suspend fun permissionState(
        permission: Permission
    ) = when (activity.permissionsGranted(permission.toAndroidPermissions())) {
        true -> PermissionState.GRANTED
        false -> PermissionState.NOT_GRANTED
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun permissionStateFlow(
        permission: Permission
    ) = activity.permissionsGrantedFlow(activity.lifecycle, permission.toAndroidPermissions())
        .mapLatest {
            when (it) {
                true -> PermissionState.GRANTED
                false -> PermissionState.NOT_GRANTED
            }
        }

    override suspend fun requestPermission(
        permission: Permission
    ) = when (permission.permissionsChecker.requestPermissions()) {
        true -> PermissionState.GRANTED
        false -> PermissionState.NOT_GRANTED
    }

    private val Permission.permissionsChecker: PermissionsChecker
        get() = permissionsCheckers[this] ?: error(
            "No PermissionsChecker for permission ${this.name}"
        )

    companion object {
        private val bluetoothPermissions = buildList {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_CONNECT)
                add(Manifest.permission.BLUETOOTH_SCAN)
            } else {
                add(Manifest.permission.BLUETOOTH)
            }
        }.toTypedArray()

        private fun Permission.toAndroidPermissions() = when (this) {
            Permission.BLUETOOTH -> bluetoothPermissions
        }
    }
}
