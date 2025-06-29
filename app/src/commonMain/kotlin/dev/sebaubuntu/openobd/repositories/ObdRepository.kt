/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.elm327.Command
import dev.sebaubuntu.openobd.elm327.Elm327Manager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * OBD repository.
 */
class ObdRepository(
    coroutineScope: CoroutineScope,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val deviceConnectionRepository: DeviceConnectionRepository,
    private val elm327Manager: Elm327Manager,
) {
    init {
        coroutineScope.launch(coroutineDispatcher) {
            deviceConnectionRepository.socket.collectLatest {
                elm327Manager.setSocket(it)
            }
        }
    }

    /**
     * @see Elm327Manager.executeCommand
     */
    suspend fun <T> executeCommand(command: Command<T>) = elm327Manager.executeCommand(command)

    /**
     * @see Elm327Manager.pollCommand
     */
    fun <T> pollCommand(
        command: Command<T>,
        pollIntervalMs: Long,
    ) = elm327Manager.pollCommand(command, pollIntervalMs)
}
