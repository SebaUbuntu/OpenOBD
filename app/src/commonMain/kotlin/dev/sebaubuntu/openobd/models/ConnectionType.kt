/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

import openobd.app.generated.resources.Res
import openobd.app.generated.resources.connection_type_bluetooth
import openobd.app.generated.resources.connection_type_network
import openobd.app.generated.resources.connection_type_serial
import openobd.app.generated.resources.connection_type_usb
import openobd.app.generated.resources.ic_bluetooth
import openobd.app.generated.resources.ic_cable
import openobd.app.generated.resources.ic_network_node
import openobd.app.generated.resources.ic_usb
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
     * TCP network device.
     */
    NETWORK(
        stringResource = Res.string.connection_type_network,
        drawableResource = Res.drawable.ic_network_node,
    ),

    /**
     * Serial.
     */
    SERIAL(
        stringResource = Res.string.connection_type_serial,
        drawableResource = Res.drawable.ic_cable,
    ),
}
