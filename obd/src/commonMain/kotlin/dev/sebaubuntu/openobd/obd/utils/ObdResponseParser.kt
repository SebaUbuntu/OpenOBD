/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.utils

import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.obd.models.ControlModule
import dev.sebaubuntu.openobd.obd.models.ObdResponse

/**
 * Utility class to parse OBD responses.
 */
@OptIn(ExperimentalStdlibApi::class, ExperimentalUnsignedTypes::class)
object ObdResponseParser {
    /**
     * Line.
     *
     * @param controlModule The control module ID
     * @param lineIndex The line index
     * @param data The OBD data
     * @param totalDataRange The data bytes range of the whole response if this is a multi-message
     *   response, usually contained in the first response
     */
    data class Line(
        val controlModule: UByte,
        val lineIndex: Int,
        val data: UByteArray,
        val totalDataRange: Pair<Int, Int>?,
    )

    private val LOG_TAG = ObdResponseParser::class.simpleName!!

    /**
     * CAN response line format:
     * - Responding control module (7E8-7EF)
     * - Number of bytes and whether this is a multi-message response
     * - Remaining response
     */
    private val canResponseRegex = "7E[8-9A-F] [0-9A-F]{2}(\\s+[0-9A-Z]{2})+".toRegex()

    /**
     * J1850 response line format:
     * - First byte is the priority
     * - Second byte is the receiver
     * - Third byte is the sender (our control module)
     * - Remaining response
     */
    private val j1850ResponseRegex = "[0-9A-F]{2}(\\s+[0-9A-Z]{2})+".toRegex()

    private val CAN_CONTROL_MODULE_ID_MASK: UShort = 0x07E8u
    private val CAN_CONTROL_MODULE_MAX_ID_VALUE: UByte = 0xFu

    private val hexFormat = HexFormat {
        bytes {
            bytesPerGroup = 1
            groupSeparator = " "
        }
        upperCase = true
    }

    fun parse(response: List<String>): ObdResponse<UByteArray>? {
        // Figure out the message format
        val messageFormat = response.firstNotNullOfOrNull {
            guessProtocol(it)
        } ?: run {
            Logger.error(LOG_TAG) { "Unable to guess message format" }
            return null
        }

        val values = buildMap<UByte, MutableMap<Int, Line>> {
            response.forEach { lineString ->
                parseLine(lineString, messageFormat)?.let { line ->
                    getOrPut(line.controlModule) { mutableMapOf() }.let {
                        if (it.containsKey(line.lineIndex)) {
                            Logger.error(LOG_TAG) { "Duplicate line index: ${line.lineIndex}" }
                            return null
                        }

                        it[line.lineIndex] = line
                    }
                }
            }
        }

        // Now join the data
        val joinedValues = buildMap {
            values.forEach { (controlModuleId, lines) ->
                val controlModule = ControlModule(controlModuleId)

                val data = lines.entries
                    .sortedBy { it.key }
                    .map { it.value.data }
                    .flatten()

                val totalDataRange = lines.values.firstNotNullOfOrNull { it.totalDataRange }

                this[controlModule] = run {
                    totalDataRange?.let { data.subList(it.first, it.second) } ?: data
                }.toUByteArray()
            }
        }

        return ObdResponse(
            value = joinedValues,
            messageFormat = messageFormat,
        )
    }

    private fun parseLine(
        line: String,
        messageFormat: ObdResponse.MessageFormat,
    ) = when (messageFormat) {
        ObdResponse.MessageFormat.CAN -> parseCanLine(line)
        ObdResponse.MessageFormat.J1850 -> parseJ1850Line(line)
    }

    private fun parseCanLine(line: String): Line? {
        if (!line.matches(canResponseRegex)) {
            Logger.error(LOG_TAG) { "Invalid CAN response line: $line" }
            return null
        }

        val (controlModuleId, messageTypeString, remainingData) = line.split(" ", limit = 3)

        val controlModule = controlModuleId.hexToUShort(format = hexFormat)
            .xor(CAN_CONTROL_MODULE_ID_MASK)
            .also { id ->
                if (id > CAN_CONTROL_MODULE_MAX_ID_VALUE) {
                    Logger.error(LOG_TAG) { "Invalid ID: $id" }
                    return null
                }
            }
            .toUByte()

        val messageType = messageTypeString.hexToUByte(format = hexFormat)

        val (thisDataBytes, lineIndex) = when {
            // Actual bytes size
            messageType <= 0x06u.toUByte() -> messageType.toInt() to 0
            // First frame
            messageType == 0x10u.toUByte() -> null to 0
            // Consecutive frame
            messageType >= 0x21u.toUByte() -> null to messageType.minus(0x20u).toInt()
            else -> error("Invalid message type: $messageType")
        }

        val totalDataBytes: String?
        val data: String
        when {
            messageType == 0x10u.toUByte() -> {
                remainingData.split(" ", limit = 2).let {
                    totalDataBytes = it[0]
                    data = it[1]
                }
            }

            else -> {
                totalDataBytes = null
                data = remainingData
            }
        }

        val totalDataRange = totalDataBytes?.let { 0 to it.hexToInt(format = hexFormat) }

        return Line(
            controlModule = controlModule,
            lineIndex = lineIndex,
            data = data.hexToUByteArray(format = hexFormat).let {
                thisDataBytes?.let { bytesCount ->
                    it.take(bytesCount).toUByteArray()
                } ?: it
            },
            totalDataRange = totalDataRange,
        )
    }

    private fun parseJ1850Line(line: String): Line? {
        if (!line.matches(j1850ResponseRegex)) {
            Logger.error(LOG_TAG) { "Invalid J1850 response line: $line" }
            return null
        }

        val (_, _, sender, data) = line.split(" ", limit = 4)

        return Line(
            controlModule = sender.hexToUByte(format = hexFormat),
            lineIndex = 0,
            data = data.hexToUByteArray(format = hexFormat),
            totalDataRange = null,
        )
    }

    /**
     * Given a line, guess the message format.
     */
    private fun guessProtocol(line: String) = when {
        line.matches(canResponseRegex) -> ObdResponse.MessageFormat.CAN
        line.matches(j1850ResponseRegex) -> ObdResponse.MessageFormat.J1850
        else -> null
    }
}
