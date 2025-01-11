/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ext

import dev.sebaubuntu.openobd.backend.models.DeviceType
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

val DeviceType.drawableResource: DrawableResource
    get() = when (this) {
        DeviceType.USB -> Res.drawable.ic_usb
        DeviceType.BLUETOOTH -> Res.drawable.ic_bluetooth
        DeviceType.NETWORK -> Res.drawable.ic_network_node
        DeviceType.SERIAL -> Res.drawable.ic_cable
    }

val DeviceType.stringResource: StringResource
    get() = when (this) {
        DeviceType.USB -> Res.string.connection_type_usb
        DeviceType.BLUETOOTH -> Res.string.connection_type_bluetooth
        DeviceType.NETWORK -> Res.string.connection_type_network
        DeviceType.SERIAL -> Res.string.connection_type_serial
    }
