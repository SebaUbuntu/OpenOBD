/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.elm327.utils

import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.elm327.models.CanResponse
import dev.sebaubuntu.openobd.elm327.models.ControlModule
import dev.sebaubuntu.openobd.logging.Logger

/**
 * Utility class to parse CAN responses.
 */
@OptIn(ExperimentalUnsignedTypes::class)
object CanResponseParser {
    /**
     * Line.
     *
     * @param controlModule The control module
     * @param frame Message frame
     */
    data class Line(
        val controlModule: ControlModule,
        val frame: ProtocolDecoder.Frame,
    )

    private val LOG_TAG = CanResponseParser::class.simpleName!!

    /**
     * ISO-TP (CAN) response line format:
     * - Responding control module (7E8-7EF)
     * - Number of bytes and whether this is a multi-message response
     * - Remaining response
     */
    private val isoTpResponseRegex = "7E[8-9A-F] [0-9A-F]{2}(\\s+[0-9A-Z]{2})+".toRegex()

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

    fun parse(response: List<String>): CanResponse<UByteArray>? {
        // Figure out the message format
        val messageFormat = response.firstNotNullOfOrNull {
            guessProtocol(it)
        } ?: run {
            Logger.error(LOG_TAG) { "Unable to guess message format" }
            return null
        }

        val values = buildMap<ControlModule, MutableList<ProtocolDecoder.Frame>> {
            response.forEach { lineString ->
                parseLine(lineString, messageFormat)?.let { line ->
                    getOrPut(line.controlModule) { mutableListOf() }.add(line.frame)
                }
            }
        }

        // Now join the data
        val joinedValues = values.mapValues { (_, frames) ->
            ProtocolDecoder.buildMessage(frames).getOrNull() ?: run {
                Logger.error(LOG_TAG) { "Unable to join frames" }
                return null
            }
        }

        return CanResponse(
            value = joinedValues,
            messageFormat = messageFormat,
        )
    }

    private fun parseLine(
        line: String,
        messageFormat: CanResponse.MessageFormat,
    ) = when (messageFormat) {
        CanResponse.MessageFormat.ISO_TP -> parseIsoTpLine(line)
        CanResponse.MessageFormat.J1850 -> parseJ1850Line(line)
    }

    private fun parseIsoTpLine(line: String): Line? {
        if (!line.matches(isoTpResponseRegex)) {
            Logger.error(LOG_TAG) { "Invalid CAN response line: $line" }
            return null
        }

        val (controlModuleId, isoTpFrameData) = line.split(" ", limit = 2)

        val controlModule = controlModuleId.hexToUShort(format = hexFormat)
            .xor(CAN_CONTROL_MODULE_ID_MASK)
            .also { id ->
                if (id > CAN_CONTROL_MODULE_MAX_ID_VALUE) {
                    Logger.error(LOG_TAG) { "Invalid ID: $id" }
                    return null
                }
            }
            .toUByte()

        return IsoTpDecoder.parseFrame(
            frame = isoTpFrameData.hexToUByteArray(format = hexFormat),
        ).getOrNull()?.let {
            Line(
                controlModule = ControlModule(controlModule),
                frame = it,
            )
        } ?: run {
            Logger.error(LOG_TAG) { "Invalid ISO-TP frame: $isoTpFrameData" }
            null
        }
    }

    private fun parseJ1850Line(line: String): Line? {
        if (!line.matches(j1850ResponseRegex)) {
            Logger.error(LOG_TAG) { "Invalid J1850 response line: $line" }
            return null
        }

        val (_, _, sender, data) = line.split(" ", limit = 4)

        return Line(
            controlModule = ControlModule(sender.hexToUByte(format = hexFormat)),
            frame = ProtocolDecoder.Frame.Single(
                data = data.hexToUByteArray(format = hexFormat),
            )
        )
    }

    /**
     * Given a line, guess the message format.
     */
    private fun guessProtocol(line: String) = when {
        line.matches(isoTpResponseRegex) -> CanResponse.MessageFormat.ISO_TP
        line.matches(j1850ResponseRegex) -> CanResponse.MessageFormat.J1850
        else -> null
    }
}
