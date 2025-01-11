/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.backend.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import dev.sebaubuntu.openobd.backend.models.BluetoothDevice
import dev.sebaubuntu.openobd.backend.models.DeviceManager
import dev.sebaubuntu.openobd.backend.models.DevicesState
import dev.sebaubuntu.openobd.backend.models.Socket
import dev.sebaubuntu.openobd.core.ext.getParcelable
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.logging.Logger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.io.Buffer
import kotlinx.io.RawSink
import kotlinx.io.RawSource
import kotlinx.io.asSink
import kotlinx.io.asSource
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@OptIn(ExperimentalUuidApi::class)
@Suppress("MissingPermission")
class AndroidBluetoothManager(private val context: Context) : BluetoothManager {
    private val bluetoothManager = context.getSystemService(
        android.bluetooth.BluetoothManager::class.java
    )

    private val bluetoothAdapter = bluetoothManager?.adapter

    override val isToggleable = bluetoothAdapter != null

    override fun state() = callbackFlow {
        val emitValue: suspend (Int) -> Unit = { value: Int ->
            val typedValue = when (value) {
                BluetoothAdapter.STATE_OFF -> DeviceManager.State.DISABLED
                BluetoothAdapter.STATE_TURNING_OFF -> DeviceManager.State.DISABLING
                BluetoothAdapter.STATE_ON -> DeviceManager.State.ENABLED
                BluetoothAdapter.STATE_TURNING_ON -> DeviceManager.State.ENABLING
                else -> error("Unknown Android Bluetooth state $value")
            }

            send(typedValue)
        }

        bluetoothAdapter?.let {
            emitValue(it.state)
        } ?: send(DeviceManager.State.UNAVAILABLE)

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    when (it.action) {
                        BluetoothAdapter.ACTION_STATE_CHANGED -> {
                            val state = it.getIntExtra(
                                BluetoothAdapter.EXTRA_STATE,
                                BluetoothAdapter.STATE_OFF
                            )

                            launch { emitValue(state) }
                        }

                        else -> {}
                    }
                }
            }
        }

        ContextCompat.registerReceiver(
            context,
            broadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )

        awaitClose {
            context.unregisterReceiver(broadcastReceiver)
        }
    }

    override fun devices() = callbackFlow {
        val mapMutex = Mutex()

        val devices = mutableMapOf<String, android.bluetooth.BluetoothDevice>()

        suspend fun android.bluetooth.BluetoothDevice.addDevice() = mapMutex.withLock {
            devices[address] = this
        }

        // Add all bonded devices to the map
        bluetoothAdapter?.bondedDevices?.forEach {
            it.addDevice()
        }

        var isSearching = false

        val emitValue = suspend {
            send(
                Result.Success<_, Error>(
                    DevicesState(
                        devices = mapMutex.withLock {
                            devices.values.map { it.toModel() }
                        },
                        isSearching = isSearching,
                    )
                )
            )
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    when (it.action) {
                        BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                            isSearching = true
                            launch { emitValue() }
                        }

                        BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                            isSearching = false
                            launch { emitValue() }
                        }

                        android.bluetooth.BluetoothDevice.ACTION_FOUND -> {
                            val device = it.extras?.getParcelable(
                                android.bluetooth.BluetoothDevice.EXTRA_DEVICE,
                                android.bluetooth.BluetoothDevice::class
                            ) ?: error("Bluetooth device data not found")

                            runBlocking { device.addDevice() }
                            launch { emitValue() }
                        }

                        else -> {}
                    }
                }
            }
        }

        emitValue()

        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
            addAction(android.bluetooth.BluetoothDevice.ACTION_FOUND)
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED,
        )

        isSearching = bluetoothAdapter?.startDiscovery() ?: false
        emitValue()

        awaitClose {
            bluetoothAdapter?.cancelDiscovery()
            context.unregisterReceiver(receiver)
        }
    }

    override fun device(
        identifier: BluetoothDevice.Identifier,
    ) = flow<Result<BluetoothDevice, Error>> {
        emit(
            bluetoothAdapter?.getRemoteDevice(identifier.macAddress)?.let {
                Result.Success(it.toModel())
            } ?: Result.Error(Error.NOT_FOUND)
        )
    }

    @OptIn(ExperimentalAtomicApi::class)
    override fun connection(
        identifier: BluetoothDevice.Identifier,
    ) = callbackFlow<Result<Socket, Error>> {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter?.cancelDiscovery()

        val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(identifier.macAddress)

        val bluetoothSocket = runCatching {
            bluetoothDevice?.createRfcommSocketToServiceRecord(
                serialUuid.toJavaUuid()
            )
        }.getOrElse {
            Logger.warn(LOG_TAG) { "Failed to create the RFCOMM socket, trying insecure variant" }

            runCatching {
                bluetoothDevice?.createInsecureRfcommSocketToServiceRecord(
                    serialUuid.toJavaUuid()
                )
            }.getOrElse {
                Logger.error(LOG_TAG, it) { "Failed to create insecure RFCOMM socket" }
                null
            }
        }

        bluetoothSocket?.also {
            runCatching {
                it.apply {
                    connect()
                }
            }.onSuccess {
                val error = AtomicBoolean(false)

                val socket = it.toSocket { throwable ->
                    if (error.compareAndSet(expectedValue = false, newValue = true)) {
                        trySendBlocking(Result.Error(Error.IO, throwable))
                    }
                }

                send(Result.Success(socket))
            }.onFailure {
                Logger.error(LOG_TAG, it) { "Failed to connect to the Bluetooth device" }
                send(Result.Error(Error.IO, it))
            }
        } ?: run {
            Logger.error(LOG_TAG) { "Failed to create the RFCOMM socket" }
            send(Result.Error(Error.IO))
        }

        awaitClose {
            runCatching {
                bluetoothSocket?.close()
            }.onFailure {
                Logger.error(LOG_TAG, it) { "Failed to close Bluetooth socket" }
            }
        }
    }

    override fun setState(state: Boolean) = bluetoothAdapter?.let {
        if (it.isEnabled != state) {
            when (state) {
                true -> {
                    context.startActivity(
                        Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                    )
                    Result.Success<_, Error>(Unit)
                }

                false -> Result.Error(Error.NOT_IMPLEMENTED)
            }
        } else {
            Result.Success(Unit)
        }
    } ?: Result.Error(Error.NOT_IMPLEMENTED)

    private fun BluetoothSocket.toSocket(
        onError: (Throwable) -> Unit,
    ): Socket {
        val rawSource = object : RawSource {
            private val realSource = inputStream.asSource()

            override fun readAtMostTo(sink: Buffer, byteCount: Long) = try {
                realSource.readAtMostTo(sink, byteCount)
            } catch (e: Throwable) {
                onError(e)
                throw e
            }

            override fun close() = realSource.close()
        }

        val rawSink = object : RawSink {
            private val realSink = outputStream.asSink()

            override fun write(source: Buffer, byteCount: Long) = try {
                realSink.write(source, byteCount)
            } catch (e: Throwable) {
                onError(e)
                throw e
            }

            override fun flush() = try {
                realSink.flush()
            } catch (e: Throwable) {
                onError(e)
                throw e
            }

            override fun close() = realSink.close()
        }

        return Socket(
            rawSource = rawSource,
            rawSink = rawSink,
        )
    }

    private fun android.bluetooth.BluetoothDevice.toModel() = BluetoothDevice(
        identifier = BluetoothDevice.Identifier(address),
        displayName = name,
        state = when (
            val connectionState = bluetoothManager.getConnectionState(
                this, BluetoothProfile.GATT
            )
        ) {
            BluetoothProfile.STATE_DISCONNECTED -> when (val bondState = bondState) {
                android.bluetooth.BluetoothDevice.BOND_NONE -> BluetoothDevice.State.AVAILABLE
                android.bluetooth.BluetoothDevice.BOND_BONDING -> BluetoothDevice.State.BONDING
                android.bluetooth.BluetoothDevice.BOND_BONDED -> BluetoothDevice.State.BONDED
                else -> error("Unknown Android Bluetooth bond state $bondState")
            }

            BluetoothProfile.STATE_CONNECTING -> BluetoothDevice.State.CONNECTING
            BluetoothProfile.STATE_CONNECTED -> BluetoothDevice.State.CONNECTED
            BluetoothProfile.STATE_DISCONNECTING -> BluetoothDevice.State.DISCONNECTING
            else -> error("Unknown Android Bluetooth connection state $connectionState")
        },
    )

    companion object {
        private val LOG_TAG = AndroidBluetoothManager::class.simpleName!!

        private val serialUuid = Uuid.parse("00001101-0000-1000-8000-00805F9B34FB")
    }
}
