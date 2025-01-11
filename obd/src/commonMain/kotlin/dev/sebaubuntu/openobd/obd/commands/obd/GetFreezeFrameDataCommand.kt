/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.obd

import dev.sebaubuntu.openobd.obd.models.Data

/**
 * Get freeze frame data command.
 *
 * @param data The sensor to get the data from
 */
class GetFreezeFrameDataCommand<T>(data: Data<T>) : GetDataCommand<T>(0x02u, data)
