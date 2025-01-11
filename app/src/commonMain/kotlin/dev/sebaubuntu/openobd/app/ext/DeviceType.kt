/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ext

import dev.sebaubuntu.openobd.backend.models.DeviceType
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.connection_type_bluetooth
import openobd.app.generated.resources.connection_type_bluetooth_le
import openobd.app.generated.resources.connection_type_demo
import openobd.app.generated.resources.connection_type_network
import openobd.app.generated.resources.connection_type_serial
import openobd.app.generated.resources.connection_type_usb
import openobd.app.generated.resources.ic_bluetooth
import openobd.app.generated.resources.ic_cable
import openobd.app.generated.resources.ic_network_node
import openobd.app.generated.resources.ic_simulation
import openobd.app.generated.resources.ic_usb
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

val DeviceType.drawableResource: DrawableResource
    get() = when (this) {
        DeviceType.USB -> Res.drawable.ic_usb
        DeviceType.BLUETOOTH -> Res.drawable.ic_bluetooth
        DeviceType.BLUETOOTH_LE -> Res.drawable.ic_bluetooth
        DeviceType.NETWORK -> Res.drawable.ic_network_node
        DeviceType.SERIAL -> Res.drawable.ic_cable
        DeviceType.DEMO -> Res.drawable.ic_simulation
    }

val DeviceType.stringResource: StringResource
    get() = when (this) {
        DeviceType.USB -> Res.string.connection_type_usb
        DeviceType.BLUETOOTH -> Res.string.connection_type_bluetooth
        DeviceType.BLUETOOTH_LE -> Res.string.connection_type_bluetooth_le
        DeviceType.NETWORK -> Res.string.connection_type_network
        DeviceType.SERIAL -> Res.string.connection_type_serial
        DeviceType.DEMO -> Res.string.connection_type_demo
    }
