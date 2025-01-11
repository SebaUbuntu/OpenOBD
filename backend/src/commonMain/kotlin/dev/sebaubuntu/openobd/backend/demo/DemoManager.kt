/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.demo

import dev.sebaubuntu.openobd.backend.models.DemoDevice
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.flatMapLatestData
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.foldLatest
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Socket
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest

class DemoManager : DeviceManager<DemoDevice, DemoDevice.Identifier> {
    private val demoDevices = mapOf<DemoDevice.Identifier, DemoDevice>()

    override fun device(
        identifier: DemoDevice.Identifier,
    ) = flowOf(
        demoDevices[identifier]?.let {
            Result.Success(it)
        } ?: Result.Error<DemoDevice, _>(Error.NOT_FOUND)
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun connection(
        identifier: DemoDevice.Identifier,
    ) = device(identifier)
        .asFlowResult()
        .flatMapLatestData { device ->
            callbackFlow {
                send(Result.Error<Socket, Error>(Error.NOT_IMPLEMENTED))
            }.mapLatest { FlowResult.Success(it) }
        }
        .foldLatest(
            onSuccess = { it },
            onError = { error, throwable -> Result.Error(error, throwable) }
        )
}
