/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.demo

import dev.sebaubuntu.openobd.backend.models.DemoDevice
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.backend.models.DevicesState
import dev.sebaubuntu.openobd.backend.models.RawSocket
import dev.sebaubuntu.openobd.core.models.FlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asFlowResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.asResult
import dev.sebaubuntu.openobd.core.models.FlowResult.Companion.flatMapLatestFlowResult
import dev.sebaubuntu.openobd.core.models.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import kotlinx.io.bytestring.decodeToString
import kotlinx.io.bytestring.encodeToByteString
import kotlinx.io.bytestring.isNotEmpty
import kotlinx.io.indexOf
import kotlinx.io.readByteString
import kotlinx.io.write

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
                val elm327Emulator = Elm327Emulator()

                val receiveBuffer = Buffer()
                val transferBuffer = Buffer()

                val receiveChannel = Channel<Unit>(1)
                val transferChannel = Channel<Unit>(1)

                val rawSocket = object : RawSocket {
                    override fun readAtMostTo(
                        sink: Buffer,
                        byteCount: Long,
                    ) = runBlocking {
                        transferChannel.receive()
                    }.let {
                        transferBuffer.readAtMostTo(sink, byteCount)
                    }

                    override fun write(
                        source: Buffer,
                        byteCount: Long,
                    ) = receiveBuffer.write(source, byteCount).also {
                        if (byteCount > 0) {
                            receiveChannel.trySend(Unit)
                        }
                    }

                    override fun flush() = receiveBuffer.flush()

                    override fun close() {
                        receiveBuffer.close()
                        transferBuffer.close()
                    }
                }

                launch(Dispatchers.IO) {
                    while (true) {
                        receiveChannel.receive()

                        if (receiveBuffer.exhausted()) {
                            continue
                        }

                        when (val lineBreakIndex = receiveBuffer.indexOf('\r'.code.toByte())) {
                            -1L -> error("Source exhausted")

                            else -> {
                                require(lineBreakIndex <= Int.MAX_VALUE) { "Too large index" }

                                // Read everything before the line break
                                val command = receiveBuffer.readByteString(lineBreakIndex.toInt())

                                // Then discard the line break
                                receiveBuffer.skip(1)

                                val response = elm327Emulator.processCommand(
                                    command.decodeToString()
                                ).encodeToByteString()

                                transferBuffer.write(response)

                                if (response.isNotEmpty()) {
                                    transferChannel.send(Unit)
                                }
                            }
                        }
                    }
                }

                send(FlowResult.Success(rawSocket))
            }
        }
        .asResult()

    override fun setState(state: Boolean) = Result.Success(Unit)
}
