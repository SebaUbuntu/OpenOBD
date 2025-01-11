/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.demo

import dev.sebaubuntu.openobd.backend.models.DemoDevice
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.backend.models.DevicesState
import dev.sebaubuntu.openobd.backend.models.Socket
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.flatMapLatestFlowResult
import dev.sebaubuntu.openobd.core.models.Result
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.RawSource

class DemoManager : DeviceManager<DemoDevice, DemoDevice.Identifier> {
    override val isToggleable = false

    override fun state() = flowOf(DeviceManager.State.ENABLED)

    override fun devices() = flowOf(
        Result.Success<_, Error>(
            DevicesState(
                listOf(DemoDevice),
                false,
            )
        )
    )

    override fun device(
        identifier: DemoDevice.Identifier,
    ) = flowOf(Result.Success<_, Error>(DemoDevice))

    override fun connection(
        identifier: DemoDevice.Identifier,
    ) = device(identifier)
        .asFlowResult()
        .flatMapLatestFlowResult { _ ->
            channelFlow {
                val elm327Emulator = Elm327Emulator()

                val commandSharedFlow = MutableSharedFlow<ByteArray>()
                val responseSharedFlow = MutableSharedFlow<ByteArray?>(replay = 1)

                val rawSource = object : RawSource {
                    override fun readAtMostTo(sink: Buffer, byteCount: Long): Long {
                        val bytes = runBlocking(Dispatchers.IO) {
                            responseSharedFlow.filterNotNull().first().also {
                                responseSharedFlow.emit(null)
                            }
                        }

                        sink.write(bytes)

                        return bytes.size.toLong()
                    }

                    override fun close() {
                        // Nothing
                    }
                }

                val rawSink = object : RawSink {
                    override fun write(source: Buffer, byteCount: Long) {
                        runBlocking(Dispatchers.IO) {
                            commandSharedFlow.emit(source.readBytes())
                        }
                    }

                    override fun flush() {
                        // Nothing
                    }

                    override fun close() {
                        // Nothing
                    }
                }

                launch(Dispatchers.IO) {
                    commandSharedFlow.collectLatest { command ->
                        val response = elm327Emulator.processCommand(command.decodeToString())

                        responseSharedFlow.emit(response.encodeToByteArray())
                    }
                }

                send(
                    FlowResult.Success(
                        Socket(
                            rawSource = rawSource,
                            rawSink = rawSink,
                        )
                    )
                )
            }
        }
        .asResult()

    override fun setState(state: Boolean) = Result.Success<Unit, Error>(Unit)
}
