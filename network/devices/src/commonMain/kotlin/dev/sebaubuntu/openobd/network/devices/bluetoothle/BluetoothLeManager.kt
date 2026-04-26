/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.network.devices.bluetoothle

import com.juul.kable.Advertisement
import com.juul.kable.Bluetooth
import com.juul.kable.ExperimentalApi
import com.juul.kable.Peripheral
import com.juul.kable.Scanner
import com.juul.kable.State
import com.juul.kable.read
import com.juul.kable.write
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.network.core.RawSocket
import dev.sebaubuntu.openobd.network.devices.models.BluetoothLeDevice
import dev.sebaubuntu.openobd.network.devices.models.DeviceManager
import dev.sebaubuntu.openobd.network.devices.models.DevicesState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlin.uuid.ExperimentalUuidApi

/**
 * Bluetooth LE manager.
 */
@OptIn(
    ExperimentalApi::class,
    ExperimentalCoroutinesApi::class,
    ExperimentalUuidApi::class,
)
class BluetoothLeManager(
    coroutineScope: CoroutineScope,
) : DeviceManager<BluetoothLeDevice, BluetoothLeDevice.Identifier> {
    private val scanner = Scanner {
        filters {
            match {
                services = listOf(serviceUuid)
            }
        }
    }

    val advertisements = scanner.advertisements.scan(
        mutableMapOf<String, Advertisement>()
    ) { advertisements, advertisement ->
        advertisements.apply {
            put(advertisement.identifier.toString(), advertisement)
        }
    }
        .flowOn(Dispatchers.IO)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = mapOf(),
        )

    override val isToggleable = false

    @OptIn(ExperimentalApi::class)
    override fun state() = suspend {
        when (Bluetooth.isSupported()) {
            true -> DeviceManager.State.ENABLED
            false -> DeviceManager.State.UNAVAILABLE
        }
    }.asFlow()

    @OptIn(ExperimentalUuidApi::class, ExperimentalCoroutinesApi::class)
    override fun devices() = advertisements.mapLatest { advertisements ->
        Result.Success(
            DevicesState(
                devices = advertisements.map { (_, advertisement) -> advertisement.toDevice() },
                isSearching = true,
            )
        )
    }

    override fun device(identifier: BluetoothLeDevice.Identifier) = {
        Result.Success(identifier.advertisement.toDevice())
    }.asFlow()

    override fun connection(identifier: BluetoothLeDevice.Identifier) = callbackFlow {
        val peripheral = Peripheral(identifier.advertisement)

        peripheral.connect()

        peripheral.state.collectLatest { state ->
            if (state !is State.Connected) {
                send(Result.Failure(Error.IO))
                return@collectLatest
            }

            val service = peripheral.services.value.orEmpty().firstOrNull {
                it.serviceUuid == serviceUuid
            } ?: run {
                send(Result.Failure(Error.IO))
                return@collectLatest
            }

            val characteristic = service.characteristics.firstOrNull {
                it.properties.read && it.properties.write
            } ?: run {
                send(Result.Failure(Error.IO))
                return@collectLatest
            }

            val socket = object : RawSocket {
                override fun readAtMostTo(sink: Buffer, byteCount: Long) = runBlocking {
                    val data = peripheral.read(characteristic)
                    sink.write(data)
                    data.size.toLong()
                }

                override fun write(source: Buffer, byteCount: Long) = runBlocking {
                    peripheral.write(characteristic, source.readByteArray())
                }

                override fun flush() {
                    // Do nothing
                }

                override fun close() {
                    // Do nothing
                }
            }

            send(Result.Success(socket))
        }

        awaitClose {
            peripheral.close()
        }
    }

    override fun setState(state: Boolean) = Result.Failure(Error.NOT_IMPLEMENTED)

    private fun Advertisement.toDevice() = BluetoothLeDevice(
        identifier = BluetoothLeDevice.Identifier(this),
        displayName = name,
    )

    companion object {
        private val serviceUuid = Bluetooth.BaseUuid + 0x0000
    }
}
