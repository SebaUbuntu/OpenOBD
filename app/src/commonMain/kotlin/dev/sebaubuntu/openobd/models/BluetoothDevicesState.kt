/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

data class BluetoothDevicesState(
    val isSearching: Boolean,
    val devices: List<BluetoothDevice>,
) {
    companion object {
        val EMPTY = BluetoothDevicesState(
            isSearching = false,
            devices = listOf(),
        )
    }
}
