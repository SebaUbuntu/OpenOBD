/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

/**
 * Vehicle information type.
 */
@OptIn(ExperimentalUnsignedTypes::class)
sealed class VehicleInformationType<T>(
    val parameterId: UByte,
    val expectedBytes: Int?,
    val responseParser: (UByteArray) -> T,
) {
    /**
     * Service 9 supported PIDs ($01 to $20).
     */
    data object PID_SUPPORTED_01_20 : VehicleInformationType<SupportedParameterIds>(
        parameterId = 0x00u,
        expectedBytes = 4,
        responseParser = getSupportedParameterIdsParser(0x00u),
    )

    /**
     * VIN Message Count in PID 02. Only for ISO 9141-2, ISO 14230-4 and SAE J1850.
     */
    data object VIN_MESSAGE_COUNT : VehicleInformationType<Int>(
        parameterId = 0x01u,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt()
        },
    )

    /**
     * Vehicle Identification Number (VIN).
     */
    data object VIN : VehicleInformationType<String>(
        parameterId = 0x02u,
        expectedBytes = null,
        responseParser = {
            it.toByteArray().decodeToString()
        },
    )

    /**
     * Calibration ID message count for PID 04. Only for ISO 9141-2, ISO 14230-4 and SAE J1850.
     */
    data object CALIBRATION_ID_MESSAGE_COUNT : VehicleInformationType<Int>(
        parameterId = 0x03u,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt()
        },
    )

    /**
     * Calibration ID.
     */
    data object CALIBRATION_ID : VehicleInformationType<List<String>>(
        parameterId = 0x04u,
        expectedBytes = null,
        responseParser = {
            it.chunked(16).map { message ->
                message.toUByteArray().toByteArray().decodeToString()
            }
        },
    )

    /**
     * Calibration verification numbers (CVN) message count for PID 06. Only for ISO 9141-2, ISO
     * 14230-4 and SAE J1850.
     */
    data object CVN_MESSAGE_COUNT : VehicleInformationType<Int>(
        parameterId = 0x05u,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt()
        },
    )

    /**
     * Calibration Verification Numbers (CVN). Several CVN can be output (4 bytes each). The number
     * of CVN and CALID must match.
     */
    data object CVN : VehicleInformationType<List<String>>(
        parameterId = 0x06u,
        expectedBytes = null,
        responseParser = {
            it.chunked(4).map { message ->
                message.toUByteArray().toHexString(format = cvnHexFormat)
            }
        },
    )

    /**
     * In-use performance tracking message count for PID 08 and 0B. Only for ISO 9141-2, ISO
     * 14230-4 and SAE J1850.
     */
    data object IN_USE_PERFORMANCE_TRACKING_MESSAGE_COUNT : VehicleInformationType<Int>(
        parameterId = 0x07u,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt()
        },
    )

    /**
     * In-use performance tracking for spark ignition vehicles.
     */
    data object IN_USE_PERFORMANCE_TRACKING_SPARK :
        VehicleInformationType<InUsePerformanceTracking>(
            parameterId = 0x08u,
            expectedBytes = null,
            responseParser = {
                InUsePerformanceTracking.fromObdValue(
                    it, InUsePerformanceTracking.EngineType.SPARK
                ) ?: error("Invalid response")
            },
        )

    /**
     * ECU name message count.
     */
    data object ECU_NAME_MESSAGE_COUNT : VehicleInformationType<Int>(
        parameterId = 0x09u,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt()
        },
    )

    /**
     * ECU name.
     */
    data object ECU_NAME : VehicleInformationType<String>(
        parameterId = 0x0Au,
        expectedBytes = null,
        responseParser = {
            it.toByteArray().decodeToString()
        },
    )

    /**
     * In-use performance tracking for compression ignition vehicles.
     */
    data object IN_USE_PERFORMANCE_TRACKING_COMPRESSION :
        VehicleInformationType<InUsePerformanceTracking>(
            parameterId = 0x0Bu,
            expectedBytes = null,
            responseParser = {
                InUsePerformanceTracking.fromObdValue(
                    it, InUsePerformanceTracking.EngineType.COMPRESSION
                ) ?: error("Invalid response")
            },
        )

    companion object {
        val all by lazy {
            listOf<VehicleInformationType<*>>(
                PID_SUPPORTED_01_20,
                VIN_MESSAGE_COUNT,
                VIN,
                CALIBRATION_ID_MESSAGE_COUNT,
                CALIBRATION_ID,
                CVN_MESSAGE_COUNT,
                CVN,
                IN_USE_PERFORMANCE_TRACKING_MESSAGE_COUNT,
                IN_USE_PERFORMANCE_TRACKING_SPARK,
                ECU_NAME_MESSAGE_COUNT,
                ECU_NAME,
                IN_USE_PERFORMANCE_TRACKING_COMPRESSION,
            ).associateBy(VehicleInformationType<*>::parameterId)
        }

        /**
         * Get a [VehicleInformationType] from a [parameterId].
         */
        fun fromParameterId(parameterId: UByte) = all[parameterId]

        private fun getSupportedParameterIdsParser(offset: UByte) = { response: UByteArray ->
            SupportedParameterIds.fromObdValue(
                response[0].toUInt().shl(24)
                    .plus(response[1].toUInt().shl(16))
                    .plus(response[2].toUInt().shl(8))
                    .plus(response[3].toUInt()),
                offset,
            )
        }

        private val cvnHexFormat = HexFormat {
            bytes {
                bytesPerGroup = 1
                groupSeparator = " "
            }
            upperCase = true
        }
    }
}
