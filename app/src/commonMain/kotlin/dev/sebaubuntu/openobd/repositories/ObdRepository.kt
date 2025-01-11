/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.repositories

import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.getOrNull
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.mapLatestData
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.elm327.Command
import dev.sebaubuntu.openobd.elm327.Elm327Session
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import kotlin.time.ExperimentalTime

/**
 * OBD repository.
 */
class ObdRepository(
    coroutineScope: CoroutineScope,
    private val coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
    deviceConnectionRepository: DeviceConnectionRepository,
) {
    @OptIn(ExperimentalTime::class)
    private val elm327Session = deviceConnectionRepository.connection
        .mapLatestData {
            Elm327Session(
                socket = it,
            ).also { elm327session ->
                elm327session.init()
            }
        }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = FlowResult.Loading(),
        )

    /**
     * @see Elm327Session.sessionInfo
     */
    val sessionInfo = elm327Session
        .mapLatestData {
            it.getSessionInfo()
        }
        .flowOn(coroutineDispatcher)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.Eagerly,
            initialValue = FlowResult.Loading(),
        )

    /**
     * @see Elm327Session.executeCommand
     */
    suspend fun <T> executeCommand(
        command: Command<T>,
    ) = withContext(coroutineDispatcher) {
        elm327Session.value.getOrNull()?.executeCommand(command) ?: Result.Error(Error.IO)
    }

    /**
     * @see Elm327Session.pollCommand
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun <T> pollCommand(
        command: Command<T>,
        pollIntervalMs: Long?,
    ) = elm327Session
        .asResult()
        .flatMapLatest {
            when (it) {
                is Result.Success -> it.data.pollCommand(command, pollIntervalMs)
                is Result.Error -> flowOf(Result.Error(it.error, it.throwable))
            }
        }
        .flowOn(coroutineDispatcher)
}
