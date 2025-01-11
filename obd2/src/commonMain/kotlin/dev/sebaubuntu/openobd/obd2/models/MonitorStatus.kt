/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

import dev.sebaubuntu.openobd.core.ext.getBit

/**
 * Base monitor status.
 */
sealed interface MonitorStatus {
    /**
     * Status of test.
     */
    enum class TestStatus {
        /**
         * Unavailable result.
         */
        UNAVAILABLE,

        /**
         * Test passed.
         */
        PASSED,

        /**
         * Test failed.
         */
        FAILED;

        companion object {
            fun fromObdValue(
                available: Boolean,
                completed: Boolean,
            ) = when (available) {
                true -> when (completed) {
                    true -> PASSED
                    false -> FAILED
                }

                false -> UNAVAILABLE
            }
        }
    }

    /**
     * State of the CEL/MIL (on/off).
     */
    val milIlluminated: Boolean

    /**
     * Number of confirmed emissions-related DTCs available for display
     */
    val currentEmissionsDtcCount: Int

    /**
     * Components test information.
     */
    val components: TestStatus

    /**
     * Fuel system test information.
     */
    val fuelSystem: TestStatus

    /**
     * Misfire test information.
     */
    val misfire: TestStatus

    /**
     * Spark ignition (e.g. Otto or Wankel engines) monitor status.
     *
     * @param egrOrVvt EGR and/or VVT system
     * @param oxygenSensorHeater Oxygen sensor heater
     * @param oxygenSensor Oxygen sensor
     * @param gasolineParticulateFilter Gasoline particulate filter
     * @param secondaryAirSystem Secondary air system
     * @param evaporativeSystem Evaporative system
     * @param heatedCatalyst Heated catalyst
     * @param catalyst Catalyst
     */
    data class SparkIgnitionMonitorStatus(
        override val milIlluminated: Boolean,
        override val currentEmissionsDtcCount: Int,
        override val components: TestStatus,
        override val fuelSystem: TestStatus,
        override val misfire: TestStatus,
        val egrOrVvt: TestStatus,
        val oxygenSensorHeater: TestStatus,
        val oxygenSensor: TestStatus,
        val gasolineParticulateFilter: TestStatus,
        val secondaryAirSystem: TestStatus,
        val evaporativeSystem: TestStatus,
        val heatedCatalyst: TestStatus,
        val catalyst: TestStatus,
    ) : MonitorStatus

    /**
     * Compression ignition (e.g. Diesel engines) monitor status.
     *
     * @param egrOrVvt EGR and/or VVT System
     * @param pmFilterMonitoring PM filter monitoring
     * @param exhaustGasSensor Exhaust gas sensor
     * @param boostPressure Boost pressure
     * @param noxScrMonitor NOx/SCR monitor
     * @param nmhcCatalyst NMHC catalyst
     */
    data class CompressionIgnitionMonitorStatus(
        override val milIlluminated: Boolean,
        override val currentEmissionsDtcCount: Int,
        override val components: TestStatus,
        override val fuelSystem: TestStatus,
        override val misfire: TestStatus,
        val egrOrVvt: TestStatus,
        val pmFilterMonitoring: TestStatus,
        val exhaustGasSensor: TestStatus,
        val boostPressure: TestStatus,
        val noxScrMonitor: TestStatus,
        val nmhcCatalyst: TestStatus,
    ) : MonitorStatus

    companion object {
        fun fromObdValue(value: UInt): MonitorStatus {
            val milIlluminated = value.getBit(31)
            val currentEmissionsDtcCount = value.shr(24).toUByte().and(0x7Fu).toInt()

            // Common tests
            val components = TestStatus.fromObdValue(
                available = value.getBit(18),
                completed = !value.getBit(22),
            )
            val fuelSystem = TestStatus.fromObdValue(
                available = value.getBit(17),
                completed = !value.getBit(21),
            )
            val misfire = TestStatus.fromObdValue(
                available = value.getBit(16),
                completed = !value.getBit(20),
            )

            // Engine-specific tests
            val test1 = TestStatus.fromObdValue(
                available = value.getBit(15),
                completed = !value.getBit(7),
            )
            val test2 = TestStatus.fromObdValue(
                available = value.getBit(14),
                completed = !value.getBit(6),
            )
            val test3 = TestStatus.fromObdValue(
                available = value.getBit(13),
                completed = !value.getBit(5),
            )
            val test4 = TestStatus.fromObdValue(
                available = value.getBit(12),
                completed = !value.getBit(4),
            )
            val test5 = TestStatus.fromObdValue(
                available = value.getBit(11),
                completed = !value.getBit(3),
            )
            val test6 = TestStatus.fromObdValue(
                available = value.getBit(10),
                completed = !value.getBit(2),
            )
            val test7 = TestStatus.fromObdValue(
                available = value.getBit(9),
                completed = !value.getBit(1),
            )
            val test8 = TestStatus.fromObdValue(
                available = value.getBit(8),
                completed = !value.getBit(0),
            )

            return when (value.getBit(19)) {
                false -> SparkIgnitionMonitorStatus(
                    milIlluminated = milIlluminated,
                    currentEmissionsDtcCount = currentEmissionsDtcCount,
                    components = components,
                    fuelSystem = fuelSystem,
                    misfire = misfire,
                    egrOrVvt = test1,
                    oxygenSensorHeater = test2,
                    oxygenSensor = test3,
                    gasolineParticulateFilter = test4,
                    secondaryAirSystem = test5,
                    evaporativeSystem = test6,
                    heatedCatalyst = test7,
                    catalyst = test8,
                )

                true -> CompressionIgnitionMonitorStatus(
                    milIlluminated = milIlluminated,
                    currentEmissionsDtcCount = currentEmissionsDtcCount,
                    components = components,
                    fuelSystem = fuelSystem,
                    misfire = misfire,
                    egrOrVvt = test1,
                    pmFilterMonitoring = test2,
                    exhaustGasSensor = test3,
                    boostPressure = test5,
                    noxScrMonitor = test7,
                    nmhcCatalyst = test8,
                )
            }
        }
    }
}
