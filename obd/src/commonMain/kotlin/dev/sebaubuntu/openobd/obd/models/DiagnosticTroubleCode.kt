/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.obd.models

import dev.sebaubuntu.openobd.logging.Logger

/**
 * Diagnostic Trouble Code.
 *
 * [Wikipedia](https://en.wikipedia.org/wiki/On-board_diagnostics#OBD-II_diagnostic_trouble_codes)
 */
@OptIn(ExperimentalStdlibApi::class)
data class DiagnosticTroubleCode(val code: UShort) : Comparable<DiagnosticTroubleCode> {
    enum class System(
        val value: UByte,
        val prefix: Char,
    ) {
        /**
         * Powertrain (engine, transmission and ignition).
         */
        POWERTRAIN(
            value = 0x0u,
            prefix = 'P'
        ),

        /**
         * Chassis (includes ABS and brake fluid).
         */
        CHASSIS(
            value = 0x1u,
            prefix = 'C',
        ),

        /**
         * Body (includes air conditioning and airbag).
         */
        BODY(
            value = 0x2u,
            prefix = 'B',
        ),

        /**
         * Network (wiring bus).
         */
        NETWORK(
            value = 0x3u,
            prefix = 'U',
        );

        companion object {
            fun fromValue(prefix: UByte) = entries.firstOrNull {
                it.value == prefix
            }

            fun fromPrefix(prefix: Char) = entries.firstOrNull {
                it.prefix == prefix
            }
        }
    }

    enum class Type {
        /**
         * Generic (SAE defined) code.
         */
        GENERIC,

        /**
         * Manufacturer-specific (OEM) code.
         */
        OEM_SPECIFIC,

        /**
         * A code that has been 'jointly' defined.
         */
        JOINTLY_DEFINED,

        /**
         * Reserved for future use.
         */
        FUTURE_USE;

        companion object {
            fun fromValue(value: UByte, componentType: System) = when (value) {
                0x0u.toUByte() -> GENERIC
                0x1u.toUByte() -> OEM_SPECIFIC
                0x2u.toUByte() -> when (componentType) {
                    System.POWERTRAIN -> GENERIC
                    else -> OEM_SPECIFIC
                }

                0x3u.toUByte() -> when (componentType) {
                    System.POWERTRAIN -> JOINTLY_DEFINED
                    else -> FUTURE_USE
                }

                else -> {
                    Logger.error(LOG_TAG) { "Invalid code type: $value" }
                    null
                }
            }
        }
    }

    enum class Subsystem {
        /**
         * Fuel and air metering and auxiliary emission controls.
         */
        FUEL_AIR_EMISSIONS,

        /**
         * Fuel and air metering.
         */
        FUEL_AIR,

        /**
         * Fuel and air metering (injector circuit).
         */
        INJECTOR_CIRCUIT,

        /**
         * Ignition systems or misfires.
         */
        IGNITION,

        /**
         * Auxiliary emission controls.
         */
        AUXILIARY_EMISSIONS,

        /**
         * Vehicle speed control and idle control systems.
         */
        SPEED_CONTROL,

        /**
         * Computer and output circuit.
         */
        COMPUTER,

        /**
         * Transmission.
         */
        TRANSMISSION,

        /**
         * Hybrid Trouble Codes.
         */
        HYBRID;

        companion object {
            fun fromValue(value: UByte) = when (value) {
                0x00u.toUByte() -> FUEL_AIR_EMISSIONS
                0x01u.toUByte() -> FUEL_AIR
                0x02u.toUByte() -> INJECTOR_CIRCUIT
                0x03u.toUByte() -> IGNITION
                0x04u.toUByte() -> AUXILIARY_EMISSIONS
                0x05u.toUByte() -> SPEED_CONTROL
                0x06u.toUByte() -> COMPUTER
                0x07u.toUByte(), 0x08U.toUByte() -> TRANSMISSION
                in 0x0Au.toUByte()..0x0Fu.toUByte() -> HYBRID
                else -> {
                    Logger.error(LOG_TAG) { "Invalid vehicle system code: $value" }
                    null
                }
            }
        }
    }

    /**
     * DTC system code. In range 0-3.
     * @see system
     */
    val systemCode = code.toInt().ushr(14).and(0x3).toUByte()

    /**
     * DTC type code. In range 0-3.
     * @see type
     */
    val typeCode = code.toInt().ushr(12).and(0x3).toUByte()

    /**
     * DTC subsystem code. In range 0-15
     * @see subsystem
     */
    val subsystemCode = code.toInt().ushr(8).and(0xF).toUByte()

    /**
     * DTC problem code. In range 0-255
     */
    val problemCode = code.toInt().and(0xFF).toUByte()

    /**
     * @see System
     */
    val system = System.fromValue(systemCode) ?: error(
        "Invalid component type: $code"
    )

    /**
     * @see Type
     */
    val type = Type.fromValue(typeCode, system)

    /**
     * @see Subsystem
     */
    val subsystem = Subsystem.fromValue(subsystemCode)

    /**
     * The string representation of this code
     */
    override fun toString() = buildString {
        append(system.prefix)
        append(typeCode.toHexString(format = nibbleHexFormat))
        append(subsystemCode.toHexString(format = nibbleHexFormat))
        append(problemCode.toHexString(format = byteHexFormat))
    }

    override fun compareTo(other: DiagnosticTroubleCode) = compareValuesBy(
        this, other,
        DiagnosticTroubleCode::code,
    )

    companion object {
        private val LOG_TAG = DiagnosticTroubleCode::class.simpleName!!

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
    }
}
