/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.bluetooth

data class BluetoothSearchState(
    val isSearching: Boolean,
    val devices: List<BluetoothDevice>,
) {
    companion object {
        val EMPTY = BluetoothSearchState(
            isSearching = false,
            devices = listOf(),
        )
    }
}
