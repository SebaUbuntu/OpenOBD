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
import dev.sebaubuntu.openobd.ext.getParcelable
import dev.sebaubuntu.openobd.models.BluetoothDevice
import dev.sebaubuntu.openobd.models.BluetoothDevicesState
import dev.sebaubuntu.openobd.models.BluetoothState
import dev.sebaubuntu.openobd.models.ConnectionType
import dev.sebaubuntu.openobd.models.Error
import dev.sebaubuntu.openobd.models.ObdDevice
import dev.sebaubuntu.openobd.models.PlatformContext
import dev.sebaubuntu.openobd.models.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid

@OptIn(ExperimentalUuidApi::class)
@Suppress("MissingPermission")
class AndroidBluetoothManager(private val platformContext: PlatformContext) : BluetoothManager {
    private val bluetoothManager = platformContext.getSystemService(
        android.bluetooth.BluetoothManager::class.java
    )

    private val bluetoothAdapter = bluetoothManager?.adapter

    override fun state() = callbackFlow {
        val emitValue = { value: Int ->
            val typedValue = when (value) {
                BluetoothAdapter.STATE_OFF -> BluetoothState.DISABLED
                BluetoothAdapter.STATE_TURNING_OFF -> BluetoothState.DISABLING
                BluetoothAdapter.STATE_ON -> BluetoothState.ENABLED
                BluetoothAdapter.STATE_TURNING_ON -> BluetoothState.ENABLING
                else -> error("Unknown Android Bluetooth state $value")
            }

            trySend(typedValue)
        }

        bluetoothAdapter?.let {
            emitValue(it.state)
        } ?: trySend(BluetoothState.UNAVAILABLE)

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let {
                    when (it.action) {
                        BluetoothAdapter.ACTION_STATE_CHANGED -> {
                            val state = it.getIntExtra(
                                BluetoothAdapter.EXTRA_STATE,
                                BluetoothAdapter.STATE_OFF
                            )

                            emitValue(state)
                        }

                        else -> {}
                    }
                }
            }
        }

        ContextCompat.registerReceiver(
            platformContext,
            broadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )

        awaitClose {
            platformContext.unregisterReceiver(broadcastReceiver)
        }
    }

    override fun devices() = callbackFlow {
        var isSearching = false
        val devices = mutableMapOf<String, android.bluetooth.BluetoothDevice>().apply {
            bluetoothAdapter?.bondedDevices?.forEach {
                put(it.address, it)
            }
        }

        val emitValue = {
            trySend(
                BluetoothDevicesState(
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
                            emitValue()
                        }

                        BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                            isSearching = false
                            emitValue()
                        }

                        android.bluetooth.BluetoothDevice.ACTION_FOUND -> {
                            val device = it.extras?.getParcelable(
                                android.bluetooth.BluetoothDevice.EXTRA_DEVICE,
                                android.bluetooth.BluetoothDevice::class
                            ) ?: error("Bluetooth device data not found")

                            devices[device.address] = device
                            emitValue()
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
            platformContext,
            receiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED,
        )

        isSearching = bluetoothAdapter?.startDiscovery() ?: false
        emitValue()

        awaitClose {
            bluetoothAdapter?.cancelDiscovery()
            platformContext.unregisterReceiver(receiver)
        }
    }

    override fun connect(macAddress: String) = callbackFlow<Result<ObdDevice, Error>> {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter?.cancelDiscovery()

        val bluetoothDevice = bluetoothAdapter?.getRemoteDevice(macAddress)

        val bluetoothSocket = bluetoothDevice?.createRfcommSocketToServiceRecord(
            serialUuid.toJavaUuid()
        )

        bluetoothSocket?.let {
            runCatching {
                it.connect()

                ObdDevice(
                    name = bluetoothDevice.name,
                    connectionType = ConnectionType.BLUETOOTH,
                    socket = it.toSocket(),
                )
            }.getOrElse {
                Logger.error(LOG_TAG, it) { "Failed to connect to Bluetooth device" }
                null
            }
        }.let {
            trySend(
                it?.let { Result.Success(it) } ?: Result.Error(Error.IO)
            )
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
                    platformContext.startActivity(
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

    private fun BluetoothSocket.toSocket() = object : ObdDevice.Socket {
        override fun read(byteArray: ByteArray) = inputStream.read(byteArray)
        override fun write(data: ByteArray) = outputStream.write(data)
    }

    private fun android.bluetooth.BluetoothDevice.toModel() = BluetoothDevice(
        name = name,
        macAddress = address,
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

        private const val SERIAL_UUID = "00001101-0000-1000-8000-00805F9B34FB"
        private val serialUuid = Uuid.parse(SERIAL_UUID)
    }
}
