/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.devices.demo

import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.flatMapLatestFlowResult
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.network.devices.models.DemoDevice
import dev.sebaubuntu.openobd.network.devices.models.DeviceManager
import dev.sebaubuntu.openobd.network.devices.models.DevicesState
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOf
import org.koin.core.annotation.Single

@Single
class DemoManager : DeviceManager<DemoDevice, DemoDevice.Identifier> {
    override val isToggleable = false

    override fun state() = flowOf(DeviceManager.State.ENABLED)

    override fun devices() = flowOf(
        Result.Success(
            DevicesState(
                listOf(DemoDevice),
                false,
            )
        )
    )

    override fun device(
        identifier: DemoDevice.Identifier,
    ) = flowOf(Result.Success(DemoDevice))

    override fun connection(
        identifier: DemoDevice.Identifier,
    ) = device(identifier)
        .asFlowResult()
        .flatMapLatestFlowResult { _ ->
            channelFlow {
                val elm327Emulator = Elm327Emulator(this)

                send(FlowResult.Success(elm327Emulator))
            }
        }
        .asResult()

    override fun setState(state: Boolean) = Result.Success(Unit)
}
