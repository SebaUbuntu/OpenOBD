/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.JvmInline

/**
 * Diagnostic Trouble Code.
 *
 * [Wikipedia](https://en.wikipedia.org/wiki/On-board_diagnostics#OBD-II_diagnostic_trouble_codes)
 *
 * Based on the SAE formatting:
 *   - The first character indicates the system
 *     - 'P' is powertrain
 *     - 'C' is chassis
 *     - 'B' is body
 *     - 'U' is network
 *   - The second character indicates the code type
 *     - 0 is generic (SAE defined)
 *     - 1 is OEM-specific
 *     - 2 is OEM-specific, except for powertrain codes, in which case it indicates generic
 *     - 3 is reserved for future use, except for powertrain codes, in which case it indicates
 *       jointly defined
 *   - The third character indicates the subsystem
 *     - 0 is fuel and air metering and auxiliary emission controls
 *     - 1 is fuel and air metering
 *     - 2 is fuel and air metering (injector circuit)
 *     - 3 is ignition systems or misfires
 *     - 4 is auxiliary emission controls
 *     - 5 is vehicle speed control and idle control systems
 *     - 6 is computer and output circuit
 *     - 7 and 8 is transmission
 *     - A to F is hybrid related
 *   - The fourth and the fifth characters indicates the problem code
 */
@JvmInline
@Serializable(with = DiagnosticTroubleCode.Serializer::class)
value class DiagnosticTroubleCode(val code: UShort) : Comparable<DiagnosticTroubleCode> {
    class Serializer : KSerializer<DiagnosticTroubleCode> {
        override val descriptor = PrimitiveSerialDescriptor(
            DiagnosticTroubleCode::class.qualifiedName!!, PrimitiveKind.STRING
        )

        override fun deserialize(decoder: Decoder) = fromString(
            decoder.decodeString()
        )

        override fun serialize(encoder: Encoder, value: DiagnosticTroubleCode) {
            encoder.encodeString(value.toString())
        }
    }

    enum class CodeFormat {
        /**
         * Raw hexadecimal value format (e.g. 0133).
         */
        RAW,

        /**
         * SAE standard format (e.g. P0133).
         */
        SAE,
    }

    override fun toString() = format(CodeFormat.SAE)

    override fun compareTo(other: DiagnosticTroubleCode) = compareValuesBy(
        this, other,
        DiagnosticTroubleCode::code,
    )

    /**
     * Get a [String] representation of this DTC with the specified format
     *
     * @param codeFormat The [CodeFormat] to format the code with
     * @see CodeFormat
     */
    fun format(codeFormat: CodeFormat) = when (codeFormat) {
        CodeFormat.RAW -> code.toHexString(format = shortHexFormat)
        CodeFormat.SAE -> buildString {
            with(code.toInt()) {
                append(rawToSaePrefix[ushr(12).and(0xF).toUByte()])
                append(ushr(8).and(0xF).toHexString(format = nibbleHexFormat))
                append(and(0xFF).toHexString(format = byteHexFormat))
            }
        }
    }

    companion object {
        private val nibbleHexFormat = HexFormat {
            number {
                minLength = 1
                removeLeadingZeros = true
            }
            upperCase = true
        }

        private val byteHexFormat = HexFormat {
            number {
                minLength = 2
                removeLeadingZeros = true
            }
            upperCase = true
        }

        private val shortHexFormat = HexFormat {
            number {
                minLength = 4
                removeLeadingZeros = true
            }
            upperCase = true
        }

        private val saeToRawPrefix = mapOf<String, UByte>(
            "P0" to 0x0u,
            "P1" to 0x1u,
            "P2" to 0x2u,
            "P3" to 0x3u,
            "C0" to 0x4u,
            "C1" to 0x5u,
            "C2" to 0x6u,
            "C3" to 0x7u,
            "B0" to 0x8u,
            "B1" to 0x9u,
            "B2" to 0xAu,
            "B3" to 0xBu,
            "U0" to 0xCu,
            "U1" to 0xDu,
            "U2" to 0xEu,
            "U3" to 0xFu,
        )

        private val rawToSaePrefix = saeToRawPrefix.entries.associate { (k, v) -> v to k }

        /**
         * Parses a [DiagnosticTroubleCode] from a string.
         *
         * If the string has 4 characters, it will be treated as a raw value.
         * If the string has 5 characters, it will be treated as a SAE value.
         */
        fun fromString(value: String) = when (value.length) {
            4 -> DiagnosticTroubleCode(value.hexToUShort(format = shortHexFormat))

            5 -> {
                val prefix = value.substring(0, 2)
                val subsystemCode = value[2]
                val problemCode = value.substring(3, 5)

                DiagnosticTroubleCode(
                    code = buildString {
                        append(
                            saeToRawPrefix[prefix]?.toHexString(
                                format = nibbleHexFormat,
                            ) ?: error("Unknown prefix $prefix")
                        )
                        append(subsystemCode)
                        append(problemCode)
                    }.hexToUShort(format = shortHexFormat)
                )
            }

            else -> error("Unrecognized DTC string length: ${value.length}")
        }
    }
}
