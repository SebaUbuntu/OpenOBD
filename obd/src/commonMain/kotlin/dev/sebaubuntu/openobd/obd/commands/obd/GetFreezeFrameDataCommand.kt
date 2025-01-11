/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.obd

import dev.sebaubuntu.openobd.obd.models.DataType

/**
 * Get freeze frame data command.
 *
 * @param dataType The sensor to get the data from
 */
class GetFreezeFrameDataCommand<T>(dataType: DataType<T>) : GetDataCommand<T>(0x02u, dataType)
