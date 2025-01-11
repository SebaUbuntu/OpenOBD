/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.models

/**
 * [Device]s state.
 *
 * @param devices The list of [Device]s
 * @param isSearching Whether the search is currently in progress
 */
data class DevicesState<D : Device<ID>, ID : Device.Identifier>(
    val devices: List<D>,
    val isSearching: Boolean,
)
