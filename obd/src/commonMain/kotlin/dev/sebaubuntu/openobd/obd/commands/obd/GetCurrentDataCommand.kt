/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.commands.obd

import dev.sebaubuntu.openobd.obd.models.Data

/**
 * Get current data command.
 *
 * @param data The sensor to get the data from
 */
class GetCurrentDataCommand<T>(data: Data<T>) : GetDataCommand<T>(0x01u, data)
