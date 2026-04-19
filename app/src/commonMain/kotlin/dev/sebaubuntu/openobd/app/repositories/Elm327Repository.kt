/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.repositories

import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.getOrNull
import dev.sebaubuntu.openobd.protocols.elm327.Command
import dev.sebaubuntu.openobd.protocols.elm327.Elm327Manager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration

/**
 * Elm327 repository.
 */
class Elm327Repository(
    deviceConnectionRepository: DeviceConnectionRepository,
    private val elm327Manager: Elm327Manager,
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher,
) : Repository(coroutineScope, coroutineDispatcher) {
    val status = elm327Manager.status
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = Elm327Manager.Status.IDLE,
        )

    init {
        coroutineScope.launch(coroutineDispatcher) {
            deviceConnectionRepository.connection.collectLatest { connection ->
                elm327Manager.setSocket(connection.getOrNull())
            }
        }
    }

    /**
     * @see Elm327Manager.executeCommand
     */
    suspend fun <T> executeCommand(
        command: Command<T>,
    ) = withContext(coroutineDispatcher) {
        elm327Manager.executeCommand(command)
    }

    /**
     * @see Elm327Manager.pollCommand
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> pollCommand(
        command: Command<T>,
        pollInterval: Duration?,
    ) = elm327Manager.pollCommand(command, pollInterval)
        .flowOn(coroutineDispatcher)
}
