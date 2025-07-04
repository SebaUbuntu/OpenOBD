/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothManager
import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothSearchState
import dev.sebaubuntu.openobd.backend.models.BluetoothDevice
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.core.models.Socket
import dev.sebaubuntu.openobd.ext.getParcelable
import dev.sebaubuntu.openobd.logging.Logger
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.io.asSink
import kotlinx.io.asSource
import java.util.concurrent.ConcurrentHashMap
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

    override fun device(
        identifier: BluetoothDevice.Identifier,
    ) = flow<Result<BluetoothDevice, Error>> {
        bluetoothAdapter?.getRemoteDevice(identifier.macAddress)?.let {
            emit(Result.Success(it.toModel()))
        } ?: emit(Result.Error(Error.NOT_FOUND))
    }

    override fun state() = callbackFlow {
        val emitValue: suspend (Int) -> Unit = { value: Int ->
            val typedValue = when (value) {
                BluetoothAdapter.STATE_OFF -> BluetoothManager.State.DISABLED
                BluetoothAdapter.STATE_TURNING_OFF -> BluetoothManager.State.DISABLING
                BluetoothAdapter.STATE_ON -> BluetoothManager.State.ENABLED
                BluetoothAdapter.STATE_TURNING_ON -> BluetoothManager.State.ENABLING
                else -> error("Unknown Android Bluetooth state $value")
            }

            send(typedValue)
        }

        bluetoothAdapter?.let {
            emitValue(it.state)
        } ?: send(BluetoothManager.State.UNAVAILABLE)

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

    override fun search() = callbackFlow {
        var isSearching = false
        val devices = ConcurrentHashMap<String, android.bluetooth.BluetoothDevice>().apply {
            bluetoothAdapter?.bondedDevices?.forEach {
                put(it.address, it)
            }
        }

        val emitValue = suspend {
            send(
                BluetoothSearchState(
                    isSearching = isSearching,
                    devices = devices.values.map { it.toModel() },
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

                            devices[device.address] = device
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
                it.connect()

                it.toSocket()
            }.onSuccess {
                send(Result.Success(it))
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

    override suspend fun toggle(isEnabled: Boolean) = bluetoothAdapter?.let {
        if (it.isEnabled != isEnabled) {
            when (isEnabled) {
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

    private fun BluetoothSocket.toSocket() = Socket(
        rawSource = inputStream.asSource(),
        rawSink = outputStream.asSink(),
    )

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
