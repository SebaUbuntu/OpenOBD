/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.commands

import dev.sebaubuntu.openobd.obd2.models.DataType

/**
 * OBD service 02: Get freeze frame data command.
 *
 * @param dataType The sensor to get the data from
 */
class GetFreezeFrameDataCommand<T>(dataType: DataType<T>) : GetDataCommand<T>(0x02u, dataType)
