/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import dev.sebaubuntu.openobd.backend.models.NetworkDevice
import dev.sebaubuntu.openobd.backend.models.NetworkDevice.Companion.toModel
import dev.sebaubuntu.openobd.backend.network.NetworkManager
import dev.sebaubuntu.openobd.storage.database.AppDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

/**
 * Network repository.
 */
class NetworkRepository(
    deviceManager: NetworkManager,
    appDatabase: AppDatabase,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : BaseDeviceRepository<NetworkManager, NetworkDevice, NetworkDevice.Identifier>(
    deviceManager, coroutineScope, coroutineDispatcher
) {
    private val dao = appDatabase.networkDeviceDao()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun get(
        identifier: NetworkDevice.Identifier,
    ) = dao.getById(identifier.networkDeviceId).mapLatest { it?.toModel() }

    suspend fun create(
        displayName: String,
        hostname: String,
        port: Int,
    ) = dao.create(displayName, hostname, port)

    suspend fun update(
        identifier: NetworkDevice.Identifier,
        displayName: String,
        hostname: String,
        port: Int,
    ) = dao.update(identifier.networkDeviceId, displayName, hostname, port)

    suspend fun delete(
        identifier: NetworkDevice.Identifier,
    ) = dao.delete(identifier.networkDeviceId)
}
