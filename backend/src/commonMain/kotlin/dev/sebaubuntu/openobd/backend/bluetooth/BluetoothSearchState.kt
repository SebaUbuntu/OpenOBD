/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.bluetooth

import dev.sebaubuntu.openobd.backend.models.BluetoothDevice

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
