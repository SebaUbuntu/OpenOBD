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
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.flatMapLatestFlowResult
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Socket
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf

class DemoManager : DeviceManager<DemoDevice, DemoDevice.Identifier> {
    override fun device(
        identifier: DemoDevice.Identifier,
    ) = flowOf(Result.Success<_, Error>(DemoDevice))

    override fun connection(
        identifier: DemoDevice.Identifier,
    ) = device(identifier)
        .asFlowResult()
        .flatMapLatestFlowResult { device ->
            callbackFlow {
                send(FlowResult.Error<Socket, Error>(Error.NOT_IMPLEMENTED))
            }
        }
        .asResult()
}
