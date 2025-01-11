/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.models

/**
 * Diagnostic Trouble Code.
 *
 * [Wikipedia](https://en.wikipedia.org/wiki/On-board_diagnostics#OBD-II_diagnostic_trouble_codes)
 */
data class Dtc(
    val componentType: ComponentType,
    val typeCode: Char,
    val systemCode: Char,
    val problemCode: UShort,
) {
    enum class ComponentType(
        val prefix: Char,
    ) {
        /**
         * Powertrain (engine, transmission and ignition).
         */
        POWERTRAIN('P'),

        /**
         * Chassis (includes ABS and brake fluid).
         */
        CHASSIS('C'),

        /**
         * Body (includes air conditioning and airbag).
         */
        BODY('B'),

        /**
         * Network (wiring bus).
         */
        NETWORK('U');

        companion object {
            fun fromPrefix(prefix: Char) = entries.firstOrNull {
                it.prefix == prefix
            }
        }
    }

    enum class CodeType {
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
            fun fromValue(value: Char, componentType: ComponentType) = when (value) {
                '0' -> GENERIC
                '1' -> OEM_SPECIFIC
                '2' -> when (componentType) {
                    ComponentType.POWERTRAIN -> GENERIC
                    else -> OEM_SPECIFIC
                }

                '3' -> when (componentType) {
                    ComponentType.POWERTRAIN -> JOINTLY_DEFINED
                    else -> FUTURE_USE
                }

                else -> error("Invalid code type: $value")
            }
        }
    }

    enum class VehicleSystemArea {
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
            fun fromValue(value: Char) = when (value) {
                '0' -> FUEL_AIR_EMISSIONS
                '1' -> FUEL_AIR
                '2' -> INJECTOR_CIRCUIT
                '3' -> IGNITION
                '4' -> AUXILIARY_EMISSIONS
                '5' -> SPEED_CONTROL
                '6' -> COMPUTER
                '7', '8' -> TRANSMISSION
                in 'A'..'F' -> HYBRID
                else -> error("Invalid vehicle system code: $value")
            }
        }
    }

    val code = buildString {
        append(componentType.prefix)
        append(typeCode)
        append(systemCode)
        append(problemCode.toString(16))
    }

    val codeType = CodeType.fromValue(typeCode, componentType)

    val vehicleSystemArea = VehicleSystemArea.fromValue(systemCode)

    /**
     * The string representation of this code
     */
    override fun toString() = code

    companion object {
        fun fromCode(code: String): Dtc? {
            if (code.length != 5) {
                return null
            }

            val componentType = ComponentType.fromPrefix(code.first()) ?: return null

            return Dtc(
                componentType = componentType,
                typeCode = code[1],
                systemCode = code[2],
                problemCode = code.substring(3).toUShort(16),
            )
        }
    }
}
