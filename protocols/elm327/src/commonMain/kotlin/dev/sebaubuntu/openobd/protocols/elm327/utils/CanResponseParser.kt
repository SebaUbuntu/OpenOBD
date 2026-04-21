/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.protocols.elm327.utils

import dev.sebaubuntu.openobd.core.ext.toUInt
import dev.sebaubuntu.openobd.core.models.Result.Companion.getOrNull
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.protocols.can.CanIdentifier
import dev.sebaubuntu.openobd.protocols.elm327.models.CanResponse

/**
 * Utility class to parse CAN responses.
 */
@OptIn(ExperimentalUnsignedTypes::class)
object CanResponseParser {
    /**
     * Line.
     *
     * @param canIdentifier The control module
     * @param frame Message frame
     */
    data class Line(
        val canIdentifier: CanIdentifier,
        val frame: ProtocolDecoder.Frame,
    )

    private val LOG_TAG = CanResponseParser::class.simpleName!!

    /**
     * ISO-TP (CAN) response line format:
     * - Responding control module (000-7FF)
     * - Number of bytes and whether this is a multi-message response
     * - Remaining response
     */
    private val isoTpResponseRegex = "[0-9A-F]{3} [0-9A-F]{2}(\\s+[0-9A-Z]{2})+".toRegex()

    /**
     * J1850 response line format:
     * - First byte is the priority
     * - Second byte is the receiver
     * - Third byte is the sender (our control module)
     * - Remaining response
     */
    private val j1850ResponseRegex = "[0-9A-F]{2}(\\s+[0-9A-Z]{2})+".toRegex()

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

        val values = buildMap<CanIdentifier, MutableList<ProtocolDecoder.Frame>> {
            response.forEach { lineString ->
                parseLine(lineString, messageFormat)?.let { line ->
                    getOrPut(line.canIdentifier) { mutableListOf() }.add(line.frame)
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

        return IsoTpDecoder.parseFrame(
            frame = isoTpFrameData.hexToUByteArray(format = hexFormat),
        ).getOrNull()?.let {
            Line(
                canIdentifier = CanIdentifier.Standard(controlModule),
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

        val (priority, receiver, sender, data) = line.split(" ", limit = 4)

        val canIdentifier = ubyteArrayOf(
            0x0u,
            priority.hexToUByte(format = hexFormat),
            receiver.hexToUByte(format = hexFormat),
            sender.hexToUByte(format = hexFormat),
        ).toUInt()

        return Line(
            canIdentifier = CanIdentifier.Extended(canIdentifier),
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
