/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

import openobd.composeapp.generated.resources.Res
import openobd.composeapp.generated.resources.connection_type_bluetooth
import openobd.composeapp.generated.resources.connection_type_serial
import openobd.composeapp.generated.resources.connection_type_usb
import openobd.composeapp.generated.resources.connection_type_wifi
import openobd.composeapp.generated.resources.ic_bluetooth
import openobd.composeapp.generated.resources.ic_cable
import openobd.composeapp.generated.resources.ic_usb
import openobd.composeapp.generated.resources.ic_wifi
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/**
 * Connection type.
 */
enum class ConnectionType(
    val stringResource: StringResource,
    val drawableResource: DrawableResource,
) {
    /**
     * USB.
     */
    USB(
        stringResource = Res.string.connection_type_usb,
        drawableResource = Res.drawable.ic_usb,
    ),

    /**
     * Bluetooth classic.
     */
    BLUETOOTH(
        stringResource = Res.string.connection_type_bluetooth,
        drawableResource = Res.drawable.ic_bluetooth,
    ),

    /**
     * Wi-Fi.
     */
    WIFI(
        stringResource = Res.string.connection_type_wifi,
        drawableResource = Res.drawable.ic_wifi,
    ),

    /**
     * Serial.
     */
    SERIAL(
        stringResource = Res.string.connection_type_serial,
        drawableResource = Res.drawable.ic_cable,
    ),
}
