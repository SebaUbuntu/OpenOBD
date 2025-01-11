/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.backend.models

/**
 * [Device] identifier.
 *
 * @param deviceType The [DeviceType] of the device
 */
abstract class DeviceIdentifier(
    val deviceType: DeviceType,
)
