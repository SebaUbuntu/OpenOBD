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
import io.ktor.utils.io.core.takeWhile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.io.Buffer
import kotlinx.io.readString
import kotlinx.io.writeString

class DemoManager : DeviceManager<DemoDevice, DemoDevice.Identifier> {
    private val devices = listOf(DemoDevice)

    override fun device(
        identifier: DemoDevice.Identifier,
    ) = flowOf(Result.Success<_, Error>(DemoDevice))

    override fun connection(
        identifier: DemoDevice.Identifier,
    ) = device(identifier)
        .asFlowResult()
        .flatMapLatestFlowResult { _ ->
            channelFlow {
                val inputBuffer = Buffer()
                val outputBuffer = Buffer()

                coroutineScope {
                    launch(Dispatchers.IO) {
                        inputBuffer.takeWhile {
                            val message = it.readString()

                            outputBuffer.writeString(
                                Elm327Emulator.processCommand(message)
                            )

                            true
                        }
                    }
                }

                send(
                    FlowResult.Success(
                        Socket(
                            rawSource = outputBuffer,
                            rawSink = inputBuffer,
                        )
                    )
                )
            }
        }
        .asResult()

    fun devices() = flowOf(devices)
}
