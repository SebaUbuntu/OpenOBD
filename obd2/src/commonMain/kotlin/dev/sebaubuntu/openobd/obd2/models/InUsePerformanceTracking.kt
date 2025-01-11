/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

import dev.sebaubuntu.openobd.core.ext.toUShort
import dev.sebaubuntu.openobd.logging.Logger

/**
 * In-use performance tracking.
 */
sealed interface InUsePerformanceTracking {
    /**
     * Information regarding a system.
     */
    data class Stats(
        val malfunctionsCount: UShort,
        val drivenWithMalfunctionCount: UShort,
    )

    /**
     * Engine type. Only used for [InUsePerformanceTracking.fromObdValue].
     */
    enum class EngineType {
        SPARK,
        COMPRESSION,
    }

    /**
     * In-use performance tracking for spark ignition vehicles.
     *
     * @param cat1 Catalyst Monitor Bank 1
     * @param cat2 Catalyst Monitor Bank 2
     * @param o2s1 O2 Sensor Monitor Bank 1
     * @param o2s2 O2 Sensor Monitor Bank 2
     * @param egr EGR Monitor
     * @param air AIR Monitor (Secondary Air)
     * @param evap EVAP Monitor
     * @param so2s1 Secondary O2 Sensor Monitor Bank 1
     * @param so2s2 Secondary O2 Sensor Monitor Bank 2
     */
    data class SparkInUsePerformanceTracking(
        override val obdcond: UShort?,
        override val igncntr: UShort?,
        val cat1: Stats?,
        val cat2: Stats?,
        val o2s1: Stats?,
        val o2s2: Stats?,
        val egr: Stats?,
        val air: Stats?,
        val evap: Stats?,
        val so2s1: Stats?,
        val so2s2: Stats?,
    ) : InUsePerformanceTracking

    /**
     * In-use performance tracking for compression ignition vehicles.
     *
     * @param hccat NMHC Catalyst Monitor
     * @param ncat NOx/SCR Catalyst Monitor
     * @param nads NOx Adsorber Monitor
     * @param pm PM Filter Monitor
     * @param egs Exhaust Gas Sensor Monitor
     * @param egr EGR and/or VVT Monitor
     * @param bp Boost Pressure Monitor
     * @param fuel Fuel Monitor
     */
    data class CompressionInUsePerformanceTracking(
        override val obdcond: UShort?,
        override val igncntr: UShort?,
        val hccat: Stats?,
        val ncat: Stats?,
        val nads: Stats?,
        val pm: Stats?,
        val egs: Stats?,
        val egr: Stats?,
        val bp: Stats?,
        val fuel: Stats?,
    ) : InUsePerformanceTracking

    /**
     * OBD Monitoring Conditions Encountered Counts.
     */
    val obdcond: UShort?

    /**
     * Ignition Counter.
     */
    val igncntr: UShort?

    companion object {
        private val LOG_TAG = InUsePerformanceTracking::class.simpleName!!

        @OptIn(ExperimentalUnsignedTypes::class)
        fun fromObdValue(obdValue: UByteArray, engineType: EngineType): InUsePerformanceTracking? {
            val dataItemsCount = obdValue[0].toShort()
            if (dataItemsCount % 2 != 0) {
                Logger.error(LOG_TAG) { "Value size must be a multiple of 2: $dataItemsCount" }
                return null
            }

            val expectedDataItemsBytes = dataItemsCount * 2
            val actualDataItemsBytes = obdValue.size - 1
            if (actualDataItemsBytes < expectedDataItemsBytes) {
                Logger.error(LOG_TAG) {
                    "Value size must be at least $expectedDataItemsBytes, got $actualDataItemsBytes"
                }
                return null
            }

            val dataItemsData = obdValue.sliceArray(1..expectedDataItemsBytes)

            val obdcond = when (actualDataItemsBytes >= 2) {
                true -> dataItemsData.sliceArray(0..1).toUShort()
                false -> null
            }
            val igncntr = when (actualDataItemsBytes >= 4) {
                true -> dataItemsData.sliceArray(2..3).toUShort()
                false -> null
            }

            val dataItems = dataItemsData.drop(4).chunked(4) { message ->
                val dataItems = message.chunked(2).map {
                    it.toUShort()
                }

                Stats(
                    malfunctionsCount = dataItems[0],
                    drivenWithMalfunctionCount = dataItems[1],
                )
            }

            return when (engineType) {
                EngineType.SPARK -> SparkInUsePerformanceTracking(
                    obdcond = obdcond,
                    igncntr = igncntr,
                    cat1 = dataItems.getOrNull(0),
                    cat2 = dataItems.getOrNull(1),
                    o2s1 = dataItems.getOrNull(2),
                    o2s2 = dataItems.getOrNull(3),
                    egr = dataItems.getOrNull(4),
                    air = dataItems.getOrNull(5),
                    evap = dataItems.getOrNull(6),
                    so2s1 = dataItems.getOrNull(7),
                    so2s2 = dataItems.getOrNull(8),
                )

                EngineType.COMPRESSION -> CompressionInUsePerformanceTracking(
                    obdcond = obdcond,
                    igncntr = igncntr,
                    hccat = dataItems.getOrNull(0),
                    ncat = dataItems.getOrNull(1),
                    nads = dataItems.getOrNull(2),
                    pm = dataItems.getOrNull(3),
                    egs = dataItems.getOrNull(4),
                    egr = dataItems.getOrNull(5),
                    bp = dataItems.getOrNull(6),
                    fuel = dataItems.getOrNull(7),
                )
            }
        }
    }
}
