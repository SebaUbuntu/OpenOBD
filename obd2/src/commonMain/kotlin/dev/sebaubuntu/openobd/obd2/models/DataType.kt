/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.obd2.models

import dev.sebaubuntu.openobd.core.ext.getBit
import dev.sebaubuntu.openobd.core.ext.toUInt
import dev.sebaubuntu.openobd.core.ext.toUShort
import dev.sebaubuntu.openobd.core.models.value.Angle
import dev.sebaubuntu.openobd.core.models.value.Angle.Companion.degrees
import dev.sebaubuntu.openobd.core.models.value.Current
import dev.sebaubuntu.openobd.core.models.value.Current.Companion.milliamperes
import dev.sebaubuntu.openobd.core.models.value.Frequency
import dev.sebaubuntu.openobd.core.models.value.Frequency.Companion.revolutionsPerMinute
import dev.sebaubuntu.openobd.core.models.value.Length
import dev.sebaubuntu.openobd.core.models.value.Length.Companion.kilometers
import dev.sebaubuntu.openobd.core.models.value.MassFlowRate
import dev.sebaubuntu.openobd.core.models.value.MassFlowRate.Companion.gramsPerSecond
import dev.sebaubuntu.openobd.core.models.value.Percentage
import dev.sebaubuntu.openobd.core.models.value.Percentage.Companion.fraction
import dev.sebaubuntu.openobd.core.models.value.Percentage.Companion.percent
import dev.sebaubuntu.openobd.core.models.value.Pressure
import dev.sebaubuntu.openobd.core.models.value.Pressure.Companion.kilopascals
import dev.sebaubuntu.openobd.core.models.value.Pressure.Companion.pascals
import dev.sebaubuntu.openobd.core.models.value.Speed
import dev.sebaubuntu.openobd.core.models.value.Speed.Companion.kilometersPerHour
import dev.sebaubuntu.openobd.core.models.value.Temperature
import dev.sebaubuntu.openobd.core.models.value.Temperature.Companion.celsius
import dev.sebaubuntu.openobd.core.models.value.Torque
import dev.sebaubuntu.openobd.core.models.value.Torque.Companion.newtonMeters
import dev.sebaubuntu.openobd.core.models.value.Voltage
import dev.sebaubuntu.openobd.core.models.value.Voltage.Companion.millivolts
import dev.sebaubuntu.openobd.core.models.value.Voltage.Companion.volts
import dev.sebaubuntu.openobd.core.models.value.VolumeFlowRate
import dev.sebaubuntu.openobd.core.models.value.VolumeFlowRate.Companion.litersPerHour
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Data type.
 *
 * @param parameterId OBD Parameter ID
 * @param expectedBytes Number of bytes expected in the response, null if flexible
 * @param responseParser Response parser
 */
@OptIn(ExperimentalUnsignedTypes::class)
sealed class DataType<T>(
    val parameterId: UByte,
    val expectedBytes: Int? = null,
    val responseParser: (UByteArray) -> T = { Unit as T },
) {
    /**
     * PIDs supported [$01 - $20].
     */
    data object PID_SUPPORTED_01_20 : DataType<SupportedParameterIds>(
        parameterId = 0x00u,
        expectedBytes = 4,
        responseParser = getSupportedParameterIdsParser(0x00u),
    )

    /**
     * Monitor status since DTCs cleared. (Includes malfunction indicator lamp (MIL) status and
     * number of DTCs, components tests, DTC readiness checks).
     */
    data object MONITOR_STATUS_SINCE_DTC_CLEARED : DataType<MonitorStatus>(
        parameterId = 0x01u,
        expectedBytes = 4,
        responseParser = monitorStatusParser,
    )

    /**
     * DTC that caused freeze frame to be stored.
     */
    data object DTC_THAT_CAUSED_FREEZE_FRAME_TO_BE_STORED : DataType<DiagnosticTroubleCode>(
        parameterId = 0x02u,
        expectedBytes = 2,
        responseParser = {
            DiagnosticTroubleCode(it.toUShort())
        }
    )

    /**
     * Fuel system status.
     */
    data object FUEL_SYSTEM_STATUS : DataType<Map<Int, FuelSystemStatus>>(
        parameterId = 0x03u,
        expectedBytes = 2,
        responseParser = {
            mapOf(
                0 to FuelSystemStatus.fromObdValue(it[0]),
                1 to FuelSystemStatus.fromObdValue(it[1]),
            )
        },
    )

    /**
     * Calculated engine load.
     */
    data object CALCULATED_ENGINE_LOAD : DataType<Percentage>(
        parameterId = 0x04u,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().div(255f).fraction
        },
    )

    /**
     * Engine coolant temperature.
     */
    data object ENGINE_COOLANT_TEMPERATURE : DataType<Temperature>(
        parameterId = 0x05u,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().minus(40).celsius
        },
    )

    /**
     * Short term fuel trim (STFT) — Bank 1.
     */
    data object SHORT_TERM_FUEL_TRIM_1 : DataType<Percentage>(
        parameterId = 0x06u,
        expectedBytes = 1,
        responseParser = fuelTrimParser,
    )

    /**
     * Long term fuel trim (LTFT) — Bank 1.
     */
    data object LONG_TERM_FUEL_TRIM_1 : DataType<Percentage>(
        parameterId = 0x07u,
        expectedBytes = 1,
        responseParser = fuelTrimParser,
    )

    /**
     * Short term fuel trim (STFT) — Bank 2.
     */
    data object SHORT_TERM_FUEL_TRIM_2 : DataType<Percentage>(
        parameterId = 0x08u,
        expectedBytes = 1,
        responseParser = fuelTrimParser,
    )

    /**
     * Long term fuel trim (LTFT) — Bank 2.
     */
    data object LONG_TERM_FUEL_TRIM_2 : DataType<Percentage>(
        parameterId = 0x09u,
        expectedBytes = 1,
        responseParser = fuelTrimParser,
    )

    /**
     * Fuel pressure (gauge pressure).
     */
    data object FUEL_PRESSURE : DataType<Pressure>(
        parameterId = 0x0Au,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().times(3).kilopascals
        },
    )

    /**
     * Intake manifold absolute pressure.
     */
    data object INTAKE_MANIFOLD_ABSOLUTE_PRESSURE : DataType<Pressure>(
        parameterId = 0x0Bu,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().kilopascals
        },
    )

    /**
     * Engine speed.
     */
    data object ENGINE_SPEED : DataType<Frequency>(
        parameterId = 0x0Cu,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt().div(4f).revolutionsPerMinute
        },
    )

    /**
     * Vehicle speed.
     */
    data object VEHICLE_SPEED : DataType<Speed>(
        parameterId = 0x0Du,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().kilometersPerHour
        },
    )

    /**
     * Timing advance.
     */
    data object TIMING_ADVANCE : DataType<Angle>(
        parameterId = 0x0Eu,
        expectedBytes = 1,
        responseParser = {
            it[0].toDouble().div(2)
                .minus(64)
                .degrees
        },
    )

    /**
     * Intake air temperature.
     */
    data object INTAKE_AIR_TEMPERATURE : DataType<Temperature>(
        parameterId = 0x0Fu,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().minus(40).celsius
        },
    )

    /**
     * Mass air flow sensor (MAF) air flow rate.
     */
    data object MAF_AIR_FLOW_RATE : DataType<MassFlowRate>(
        parameterId = 0x10u,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt()
                .div(100f)
                .gramsPerSecond
        },
    )

    /**
     * Throttle position.
     */
    data object THROTTLE_POSITION : DataType<Percentage>(
        parameterId = 0x11u,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().div(255f).fraction
        },
    )

    /**
     * Commanded secondary air status.
     */
    data object COMMANDED_SECONDARY_AIR_STATUS : DataType<CommandedSecondaryAirStatus>(
        parameterId = 0x12u,
        expectedBytes = 1,
        responseParser = {
            CommandedSecondaryAirStatus.fromObdValue(it[0])
        },
    )

    /**
     * Oxygen sensors present (in 2 banks).
     */
    data object OXYGEN_SENSORS_PRESENT_2_BANKS : DataType<Map<Pair<Int, Int>, Boolean>>(
        parameterId = 0x13u,
        expectedBytes = 1,
        responseParser = {
            buildMap {
                for (i in 0..7) {
                    val bank = i.floorDiv(4) + 1
                    val sensor = i.mod(4) + 1

                    put(bank to sensor, it[0].toUInt().getBit(i))
                }
            }
        },
    )

    /**
     * Oxygen Sensor 1.
     *
     * - A: Voltage
     * - B: Short term fuel trim
     */
    data object OXYGEN_SENSOR_1_V_STFT : DataType<Pair<Float, Percentage?>>(
        parameterId = 0x14u,
        expectedBytes = 2,
        responseParser = oxygenSensorVStftParser,
    )

    /**
     * Oxygen Sensor 2.
     *
     * - A: Voltage
     * - B: Short term fuel trim
     */
    data object OXYGEN_SENSOR_2_V_STFT : DataType<Pair<Float, Percentage?>>(
        parameterId = 0x15u,
        expectedBytes = 2,
        responseParser = oxygenSensorVStftParser,
    )

    /**
     * Oxygen Sensor 3.
     *
     * - A: Voltage
     * - B: Short term fuel trim
     */
    data object OXYGEN_SENSOR_3_V_STFT : DataType<Pair<Float, Percentage?>>(
        parameterId = 0x16u,
        expectedBytes = 2,
        responseParser = oxygenSensorVStftParser,
    )

    /**
     * Oxygen Sensor 4.
     *
     * - A: Voltage
     * - B: Short term fuel trim
     */
    data object OXYGEN_SENSOR_4_V_STFT : DataType<Pair<Float, Percentage?>>(
        parameterId = 0x17u,
        expectedBytes = 2,
        responseParser = oxygenSensorVStftParser,
    )

    /**
     * Oxygen Sensor 5.
     *
     * - A: Voltage
     * - B: Short term fuel trim
     */
    data object OXYGEN_SENSOR_5_V_STFT : DataType<Pair<Float, Percentage?>>(
        parameterId = 0x18u,
        expectedBytes = 2,
        responseParser = oxygenSensorVStftParser,
    )

    /**
     * Oxygen Sensor 6.
     *
     * - A: Voltage
     * - B: Short term fuel trim
     */
    data object OXYGEN_SENSOR_6_V_STFT : DataType<Pair<Float, Percentage?>>(
        parameterId = 0x19u,
        expectedBytes = 2,
        responseParser = oxygenSensorVStftParser,
    )

    /**
     * Oxygen Sensor 7.
     *
     * - A: Voltage
     * - B: Short term fuel trim
     */
    data object OXYGEN_SENSOR_7_V_STFT : DataType<Pair<Float, Percentage?>>(
        parameterId = 0x1Au,
        expectedBytes = 2,
        responseParser = oxygenSensorVStftParser,
    )

    /**
     * Oxygen Sensor 8.
     *
     * - A: Voltage
     * - B: Short term fuel trim
     */
    data object OXYGEN_SENSOR_8_V_STFT : DataType<Pair<Float, Percentage?>>(
        parameterId = 0x1Bu,
        expectedBytes = 2,
        responseParser = oxygenSensorVStftParser,
    )

    /**
     * OBD standards this vehicle conforms to.
     */
    data object OBD_STANDARDS_CONFORMING_TO : DataType<Set<ObdStandard>>(
        parameterId = 0x1Cu,
        expectedBytes = 1,
        responseParser = {
            ObdStandard.fromObdValue(it[0])
        },
    )

    /**
     * Oxygen sensors present (in 4 banks).
     */
    data object OXYGEN_SENSORS_PRESENT_4_BANKS : DataType<Map<Pair<Int, Int>, Boolean>>(
        parameterId = 0x1Du,
        expectedBytes = 1,
        responseParser = {
            buildMap {
                for (i in 0..7) {
                    val bank = i.floorDiv(2) + 1
                    val sensor = i.mod(2) + 1

                    put(bank to sensor, it[0].toUInt().getBit(i))
                }
            }
        },
    )

    /**
     * Auxiliary input status.
     */
    data object AUXILIARY_INPUT_STATUS : DataType<Boolean>(
        parameterId = 0x1Eu,
        expectedBytes = 1,
        responseParser = {
            it[0].toUInt().getBit(0)
        },
    )

    /**
     * Run time since engine start.
     */
    data object RUN_TIME_SINCE_ENGINE_START : DataType<Duration>(
        parameterId = 0x1Fu,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt().seconds
        },
    )

    /**
     * PIDs supported [$21 - $40].
     */
    data object PID_SUPPORTED_21_40 : DataType<SupportedParameterIds>(
        parameterId = 0x20u,
        expectedBytes = 4,
        responseParser = getSupportedParameterIdsParser(0x20u),
    )

    /**
     * Distance traveled with malfunction indicator lamp (MIL) on.
     */
    data object DISTANCE_TRAVELED_WITH_MIL_ON : DataType<Duration>(
        parameterId = 0x21u,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt().seconds
        },
    )

    /**
     * Fuel Rail Pressure (relative to manifold vacuum).
     */
    data object FUEL_RAIL_PRESSURE : DataType<Pressure>(
        parameterId = 0x22u,
        expectedBytes = 2,
        responseParser = {
            0.079f.times(it.toUShort().toInt()).kilopascals
        },
    )

    /**
     * Fuel Rail Gauge Pressure (diesel, or gasoline direct injection).
     */
    data object FUEL_RAIL_GAUGE_PRESSURE : DataType<Pressure>(
        parameterId = 0x23u,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt()
                .times(10)
                .kilopascals
        },
    )

    /**
     * Oxygen Sensor 1.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Voltage
     */
    data object OXYGEN_SENSOR_1_AF_V : DataType<Pair<Float, Voltage>>(
        parameterId = 0x24u,
        expectedBytes = 4,
        responseParser = oxygenSensorAfVParser,
    )

    /**
     * Oxygen Sensor 2.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Voltage
     */
    data object OXYGEN_SENSOR_2_AF_V : DataType<Pair<Float, Voltage>>(
        parameterId = 0x25u,
        expectedBytes = 4,
        responseParser = oxygenSensorAfVParser,
    )

    /**
     * Oxygen Sensor 3.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Voltage
     */
    data object OXYGEN_SENSOR_3_AF_V : DataType<Pair<Float, Voltage>>(
        parameterId = 0x26u,
        expectedBytes = 4,
        responseParser = oxygenSensorAfVParser,
    )

    /**
     * Oxygen Sensor 4.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Voltage
     */
    data object OXYGEN_SENSOR_4_AF_V : DataType<Pair<Float, Voltage>>(
        parameterId = 0x27u,
        expectedBytes = 4,
        responseParser = oxygenSensorAfVParser,
    )

    /**
     * Oxygen Sensor 5.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Voltage
     */
    data object OXYGEN_SENSOR_5_AF_V : DataType<Pair<Float, Voltage>>(
        parameterId = 0x28u,
        expectedBytes = 4,
        responseParser = oxygenSensorAfVParser,
    )

    /**
     * Oxygen Sensor 6.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Voltage
     */
    data object OXYGEN_SENSOR_6_AF_V : DataType<Pair<Float, Voltage>>(
        parameterId = 0x29u,
        expectedBytes = 4,
        responseParser = oxygenSensorAfVParser,
    )

    /**
     * Oxygen Sensor 7.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Voltage
     */
    data object OXYGEN_SENSOR_7_AF_V : DataType<Pair<Float, Voltage>>(
        parameterId = 0x2Au,
        expectedBytes = 4,
        responseParser = oxygenSensorAfVParser,
    )

    /**
     * Oxygen Sensor 8.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Voltage
     */
    data object OXYGEN_SENSOR_8_AF_V : DataType<Pair<Float, Voltage>>(
        parameterId = 0x2Bu,
        expectedBytes = 4,
        responseParser = oxygenSensorAfVParser,
    )

    /**
     * Commanded EGR.
     */
    data object COMMANDED_EGR : DataType<Percentage>(
        parameterId = 0x2Cu,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().div(255f).fraction
        },
    )

    /**
     * EGR Error.
     */
    data object EGR_ERROR : DataType<Percentage>(
        parameterId = 0x2Du,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().div(255f)
                .minus(1)
                .fraction
        },
    )

    /**
     * Commanded evaporative purge.
     */
    data object COMMANDED_EVAPORATIVE_PURGE : DataType<Percentage>(
        parameterId = 0x2Eu,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().div(255f).fraction
        },
    )

    /**
     * Fuel Tank Level Input.
     */
    data object FUEL_TANK_LEVEL_INPUT : DataType<Percentage>(
        parameterId = 0x2Fu,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().div(255f).fraction
        },
    )

    /**
     * Warm-ups since codes cleared.
     */
    data object WARM_UPS_SINCE_CODES_CLEARED : DataType<Int>(
        parameterId = 0x30u,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt()
        },
    )

    /**
     * Distance traveled since codes cleared.
     */
    data object DISTANCE_TRAVELED_SINCE_CODES_CLEARED : DataType<Length>(
        parameterId = 0x31u,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt().kilometers
        },
    )

    /**
     * Evap. System Vapor Pressure.
     */
    data object EVAPORATION_SYSTEM_VAPOR_PRESSURE : DataType<Pressure>(
        parameterId = 0x32u,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toShort().div(4f).pascals // TODO check negative
        },
    )

    /**
     * Absolute Barometric Pressure.
     */
    data object ABSOLUTE_BAROMETRIC_PRESSURE : DataType<Pressure>(
        parameterId = 0x33u,
        expectedBytes = 1,
        responseParser = {
            it[0].toShort().kilopascals
        },
    )

    /**
     * Oxygen Sensor 1.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Current
     */
    data object OXYGEN_SENSOR_1_AF_A : DataType<Pair<Float, Current>>(
        parameterId = 0x34u,
        expectedBytes = 4,
        responseParser = oxygenSensorAfAParser,
    )

    /**
     * Oxygen Sensor 2.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Current
     */
    data object OXYGEN_SENSOR_2_AF_A : DataType<Pair<Float, Current>>(
        parameterId = 0x35u,
        expectedBytes = 4,
        responseParser = oxygenSensorAfAParser,
    )

    /**
     * Oxygen Sensor 3.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Current
     */
    data object OXYGEN_SENSOR_3_AF_A : DataType<Pair<Float, Current>>(
        parameterId = 0x36u,
        expectedBytes = 4,
        responseParser = oxygenSensorAfAParser,
    )

    /**
     * Oxygen Sensor 4.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Current
     */
    data object OXYGEN_SENSOR_4_AF_A : DataType<Pair<Float, Current>>(
        parameterId = 0x37u,
        expectedBytes = 4,
        responseParser = oxygenSensorAfAParser,
    )

    /**
     * Oxygen Sensor 5.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Current
     */
    data object OXYGEN_SENSOR_5_AF_A : DataType<Pair<Float, Current>>(
        parameterId = 0x38u,
        expectedBytes = 4,
        responseParser = oxygenSensorAfAParser,
    )

    /**
     * Oxygen Sensor 6.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Current
     */
    data object OXYGEN_SENSOR_6_AF_A : DataType<Pair<Float, Current>>(
        parameterId = 0x39u,
        expectedBytes = 4,
        responseParser = oxygenSensorAfAParser,
    )

    /**
     * Oxygen Sensor 7.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Current
     */
    data object OXYGEN_SENSOR_7_AF_A : DataType<Pair<Float, Current>>(
        parameterId = 0x3Au,
        expectedBytes = 4,
        responseParser = oxygenSensorAfAParser,
    )

    /**
     * Oxygen Sensor 8.
     *
     * - AB: Air-Fuel Equivalence Ratio (lambda,λ)
     * - CD: Current
     */
    data object OXYGEN_SENSOR_8_AF_A : DataType<Pair<Float, Current>>(
        parameterId = 0x3Bu,
        expectedBytes = 4,
        responseParser = oxygenSensorAfAParser,
    )

    /**
     * Catalyst Temperature: Bank 1, Sensor 1.
     */
    data object CATALYST_TEMPERATURE_BANK_1_SENSOR_1 : DataType<Temperature>(
        parameterId = 0x3Cu,
        expectedBytes = 2,
        responseParser = catalystTemperatureParser,
    )

    /**
     * Catalyst Temperature: Bank 2, Sensor 1.
     */
    data object CATALYST_TEMPERATURE_BANK_2_SENSOR_1 : DataType<Temperature>(
        parameterId = 0x3Du,
        expectedBytes = 2,
        responseParser = catalystTemperatureParser,
    )

    /**
     * Catalyst Temperature: Bank 1, Sensor 2.
     */
    data object CATALYST_TEMPERATURE_BANK_1_SENSOR_2 : DataType<Temperature>(
        parameterId = 0x3Eu,
        expectedBytes = 2,
        responseParser = catalystTemperatureParser,
    )

    /**
     * Catalyst Temperature: Bank 2, Sensor 2.
     */
    data object CATALYST_TEMPERATURE_BANK_2_SENSOR_2 : DataType<Temperature>(
        parameterId = 0x3Fu,
        expectedBytes = 2,
        responseParser = catalystTemperatureParser,
    )

    /**
     * PIDs supported [$41 - $60].
     */
    data object PID_SUPPORTED_41_60 : DataType<SupportedParameterIds>(
        parameterId = 0x40u,
        expectedBytes = 4,
        responseParser = getSupportedParameterIdsParser(0x40u),
    )

    /**
     * Monitor status this drive cycle.
     */
    data object CURRENT_DRIVE_CYCLE_MONITOR_STATUS : DataType<MonitorStatus>(
        parameterId = 0x41u,
        expectedBytes = 4,
        responseParser = monitorStatusParser,
    )

    /**
     * Control module voltage.
     */
    data object CONTROL_MODULE_VOLTAGE : DataType<Voltage>(
        parameterId = 0x42u,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt().millivolts
        },
    )

    /**
     * Absolute load value.
     */
    data object ABSOLUTE_LOAD_VALUE : DataType<Percentage>(
        parameterId = 0x43u,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt().div(255f).fraction
        },
    )

    /**
     * Commanded Air-Fuel Equivalence Ratio (lambda,λ).
     */
    data object COMMANDED_AIR_FUEL_EQUIVALENCE_RATIO : DataType<Float>(
        parameterId = 0x44u,
        expectedBytes = 2,
        responseParser = {
            (2 / 65536f).times(it.toUShort().toInt())
        },
    )

    /**
     * Relative throttle position.
     */
    data object RELATIVE_THROTTLE_POSITION : DataType<Percentage>(
        parameterId = 0x45u,
        expectedBytes = 1,
        responseParser = positionParser,
    )

    /**
     * Ambient air temperature.
     */
    data object AMBIENT_AIR_TEMPERATURE : DataType<Temperature>(
        parameterId = 0x46u,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().minus(40).celsius
        },
    )

    /**
     * Absolute throttle position B.
     */
    data object ABSOLUTE_THROTTLE_POSITION_B : DataType<Percentage>(
        parameterId = 0x47u,
        expectedBytes = 1,
        responseParser = positionParser,
    )

    /**
     * Absolute throttle position C.
     */
    data object ABSOLUTE_THROTTLE_POSITION_C : DataType<Percentage>(
        parameterId = 0x48u,
        expectedBytes = 1,
        responseParser = positionParser,
    )

    /**
     * Accelerator pedal position D.
     */
    data object ACCELERATOR_PEDAL_POSITION_D : DataType<Percentage>(
        parameterId = 0x49u,
        expectedBytes = 1,
        responseParser = positionParser,
    )

    /**
     * Accelerator pedal position E.
     */
    data object ACCELERATOR_PEDAL_POSITION_E : DataType<Percentage>(
        parameterId = 0x4Au,
        expectedBytes = 1,
        responseParser = positionParser,
    )

    /**
     * Accelerator pedal position F.
     */
    data object ACCELERATOR_PEDAL_POSITION_F : DataType<Percentage>(
        parameterId = 0x4Bu,
        expectedBytes = 1,
        responseParser = positionParser,
    )

    /**
     * Commanded throttle actuator.
     */
    data object COMMANDED_THROTTLE_ACTUATOR : DataType<Percentage>(
        parameterId = 0x4Cu,
        expectedBytes = 1,
        responseParser = positionParser,
    )

    /**
     * Time run with MIL on.
     */
    data object TIME_RUN_WITH_MIL_ON : DataType<Duration>(
        parameterId = 0x4Du,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt().minutes
        },
    )

    /**
     * Time since trouble codes cleared.
     */
    data object TIME_SINCE_TROUBLE_CODES_CLEARED : DataType<Duration>(
        parameterId = 0x4Eu,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt().minutes
        },
    )

    /**
     * Maximum value for Fuel–Air equivalence ratio, oxygen sensor voltage, oxygen sensor current,
     * and intake manifold absolute pressure.
     */
    data object EXTERNAL_TEST_EQUIPMENT_CONFIGURATION_1 :
        DataType<ExternalTestEquipmentConfiguration1>(
            parameterId = 0x4Fu,
            expectedBytes = 4,
            responseParser = {
                ExternalTestEquipmentConfiguration1.fromObdValue(it)
            },
        )

    /**
     * Maximum value for air flow rate from mass air flow sensor.
     */
    data object EXTERNAL_TEST_EQUIPMENT_CONFIGURATION_2 :
        DataType<ExternalTestEquipmentConfiguration2>(
            parameterId = 0x50u,
            expectedBytes = 4,
            responseParser = {
                ExternalTestEquipmentConfiguration2.fromObdValue(it)
            },
        )

    /**
     * Fuel Type.
     */
    data object FUEL_TYPE : DataType<FuelType>(
        parameterId = 0x51u,
        expectedBytes = 1,
        responseParser = {
            FuelType.fromObdValue(it[0])
        },
    )

    /**
     * Ethanol fuel %.
     */
    data object ETHANOL_FUEL_PERCENTAGE : DataType<Percentage>(
        parameterId = 0x52u,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().div(255f).fraction
        },
    )

    /**
     * Absolute Evap system Vapor Pressure.
     */
    data object ABSOLUTE_EVAP_SYSTEM_VAPOR_PRESSURE : DataType<Pressure>(
        parameterId = 0x53u,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt().div(200f).kilopascals
        },
    )

    /**
     * Evap system vapor pressure.
     */
    data object EVAP_SYSTEM_VAPOR_PRESSURE : DataType<Pressure>(
        parameterId = 0x54u,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toShort().toFloat().pascals // TODO: Check negative
        },
    )

    /**
     * Short term secondary oxygen sensor trim, A: bank 1, B: bank 3.
     */
    data object SHORT_TERM_SECONDARY_OXYGEN_SENSOR_TRIM_1_3 :
        DataType<Pair<Percentage, Percentage>>(
            parameterId = 0x55u,
            expectedBytes = 2,
            responseParser = secondaryOxygenSensorTrimTwoBanksParser,
        )

    /**
     * Long term secondary oxygen sensor trim, A: bank 1, B: bank 3.
     */
    data object LONG_TERM_SECONDARY_OXYGEN_SENSOR_TRIM_1_3 : DataType<Pair<Percentage, Percentage>>(
        parameterId = 0x56u,
        expectedBytes = 2,
        responseParser = secondaryOxygenSensorTrimTwoBanksParser,
    )

    /**
     * Short term secondary oxygen sensor trim, A: bank 2, B: bank 4.
     */
    data object SHORT_TERM_SECONDARY_OXYGEN_SENSOR_TRIM_2_4 :
        DataType<Pair<Percentage, Percentage>>(
            parameterId = 0x57u,
            expectedBytes = 2,
            responseParser = secondaryOxygenSensorTrimTwoBanksParser,
        )

    /**
     * Long term secondary oxygen sensor trim, A: bank 2, B: bank 4.
     */
    data object LONG_TERM_SECONDARY_OXYGEN_SENSOR_TRIM_2_4 : DataType<Pair<Percentage, Percentage>>(
        parameterId = 0x58u,
        expectedBytes = 2,
        responseParser = secondaryOxygenSensorTrimTwoBanksParser,
    )

    /**
     * Fuel rail absolute pressure.
     */
    data object FUEL_RAIL_ABSOLUTE_PRESSURE : DataType<Pressure>(
        parameterId = 0x59u,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt()
                .times(10)
                .kilopascals
        }
    )

    /**
     * Relative accelerator pedal position.
     */
    data object RELATIVE_ACCELERATOR_PEDAL_POSITION : DataType<Percentage>(
        parameterId = 0x5Au,
        expectedBytes = 1,
        responseParser = positionParser,
    )

    /**
     * Hybrid battery pack remaining life.
     */
    data object HYBRID_BATTERY_PACK_REMAINING_LIFE : DataType<Percentage>(
        parameterId = 0x5Bu,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().div(255f).fraction
        },
    )

    /**
     * Engine oil temperature.
     */
    data object ENGINE_OIL_TEMPERATURE : DataType<Temperature>(
        parameterId = 0x5Cu,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().minus(40).celsius
        },
    )

    /**
     * Fuel injection timing.
     */
    data object FUEL_INJECTION_TIMING : DataType<Angle>(
        parameterId = 0x5Du,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt()
                .div(128.0)
                .minus(210)
                .degrees
        },
    )

    /**
     * Engine fuel rate.
     */
    data object ENGINE_FUEL_RATE : DataType<VolumeFlowRate>(
        parameterId = 0x5Eu,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt().div(20f).litersPerHour
        },
    )

    /**
     * Emission requirements to which vehicle is designed.
     */
    data object DESIGNED_EMISSION_REQUIREMENTS : DataType<EmissionRequirements>(
        parameterId = 0x5Fu,
        expectedBytes = 1,
        responseParser = {
            EmissionRequirements.fromObdValue(it[0])
        },
    )

    /**
     * PIDs supported [$61 - $80].
     */
    data object PID_SUPPORTED_61_80 : DataType<SupportedParameterIds>(
        parameterId = 0x60u,
        expectedBytes = 4,
        responseParser = getSupportedParameterIdsParser(0x60u),
    )

    /**
     * Driver's demand engine - percent torque.
     */
    data object DRIVER_DEMAND_ENGINE_PERCENT_TORQUE : DataType<Percentage>(
        parameterId = 0x61u,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().minus(125).percent
        },
    )

    /**
     * Actual engine - percent torque.
     */
    data object ACTUAL_ENGINE_PERCENT_TORQUE : DataType<Percentage>(
        parameterId = 0x62u,
        expectedBytes = 1,
        responseParser = {
            it[0].toInt().minus(125).percent
        },
    )

    /**
     * Engine reference torque.
     */
    data object ENGINE_REFERENCE_TORQUE : DataType<Torque>(
        parameterId = 0x63u,
        expectedBytes = 2,
        responseParser = {
            it.toUShort().toInt().newtonMeters
        },
    )

    /**
     * Engine percent torque data.
     */
    data object ENGINE_PERCENT_TORQUE_DATA : DataType<Nothing>(
        parameterId = 0x64u,
    )

    /**
     * Auxiliary input / output supported.
     */
    data object AUXILIARY_INPUT_OUTPUT_SUPPORTED : DataType<Nothing>(
        parameterId = 0x65u,
    )

    /**
     * Mass air flow sensor.
     */
    data object MASS_AIR_FLOW_SENSOR : DataType<Nothing>(
        parameterId = 0x66u,
    )

    /**
     * Engine coolant temperature.
     */
    data object ENGINE_COOLANT_TEMPERATURE_2_SENSORS : DataType<Nothing>(
        parameterId = 0x67u,
    )

    /**
     * Intake air temperature sensor.
     */
    data object INTAKE_AIR_TEMPERATURE_SENSOR : DataType<Nothing>(
        parameterId = 0x68u,
    )

    /**
     * Actual EGR, Commanded EGR, and EGR Error.
     */
    data object EGR_ACTUAL_COMMANDED_ERROR : DataType<Nothing>(
        parameterId = 0x69u,
    )

    /**
     * Commanded Diesel intake air flow control and relative intake air flow position.
     */
    data object COMMANDED_DIESEL_INTAKE_AIR_FLOW_CONTROL_AND_RELATIVE_INTAKE_AIR_FLOW_POSITION :
        DataType<Nothing>(
            parameterId = 0x6Au,
        )

    /**
     * Exhaust gas recirculation temperature.
     */
    data object EXHAUST_GAS_RECIRCULATION_TEMPERATURE : DataType<Nothing>(
        parameterId = 0x6Bu,
    )

    /**
     * Commanded throttle actuator control and relative throttle position.
     */
    data object COMMANDED_THROTTLE_ACTUATOR_CONTROL_AND_RELATIVE_THROTTLE_POSITION :
        DataType<Nothing>(
            parameterId = 0x6Cu,
        )

    /**
     * Fuel pressure control system.
     */
    data object FUEL_PRESSURE_CONTROL_SYSTEM : DataType<Nothing>(
        parameterId = 0x6Du,
    )

    /**
     * Injection pressure control system.
     */
    data object INJECTION_PRESSURE_CONTROL_SYSTEM : DataType<Nothing>(
        parameterId = 0x6Eu,
    )

    /**
     * Turbocharger compressor inlet pressure.
     */
    data object TURBOCHARGER_COMPRESSOR_INLET_PRESSURE : DataType<Nothing>(
        parameterId = 0x6Fu,
    )

    /**
     * Boost pressure control.
     */
    data object BOOST_PRESSURE_CONTROL : DataType<Nothing>(
        parameterId = 0x70u,
    )

    /**
     * Variable Geometry turbo (VGT) control.
     */
    data object VARIABLE_GEOMETRY_TURBO_CONTROL : DataType<Nothing>(
        parameterId = 0x71u,
    )

    /**
     * Wastegate control.
     */
    data object WASTEGATE_CONTROL : DataType<Nothing>(
        parameterId = 0x72u,
    )

    /**
     * Exhaust pressure.
     */
    data object EXHAUST_PRESSURE : DataType<Nothing>(
        parameterId = 0x73u,
    )

    /**
     * Turbocharger RPM.
     */
    data object TURBOCHARGER_RPM : DataType<Nothing>(
        parameterId = 0x74u,
    )

    /**
     * Turbocharger temperature.
     */
    data object TURBOCHARGER_A_TEMPERATURE : DataType<Nothing>(
        parameterId = 0x75u,
    )

    /**
     * Turbocharger temperature.
     */
    data object TURBOCHARGER_B_TEMPERATURE : DataType<Nothing>(
        parameterId = 0x76u,
    )

    /**
     * Charge air cooler temperature (CACT).
     */
    data object CHARGE_AIR_COOLER_TEMPERATURE : DataType<Nothing>(
        parameterId = 0x77u,
    )

    /**
     * Exhaust Gas temperature (EGT) Bank 1.
     */
    data object EXHAUST_GAS_TEMPERATURE_BANK_1 : DataType<Nothing>(
        parameterId = 0x78u,
    )

    /**
     * Exhaust Gas temperature (EGT) Bank 2.
     */
    data object EXHAUST_GAS_TEMPERATURE_BANK_2 : DataType<Nothing>(
        parameterId = 0x79u,
    )

    /**
     * Diesel particulate filter (DPF) differential pressure (bank 1).
     */
    data object DIESEL_PARTICULATE_FILTER_DIFFERENTIAL_PRESSURE_BANK_1 : DataType<Nothing>(
        parameterId = 0x7Au,
    )

    /**
     * Diesel particulate filter (DPF) differential pressure (bank 2).
     */
    data object DIESEL_PARTICULATE_FILTER_DIFFERENTIAL_PRESSURE_BANK_2 : DataType<Nothing>(
        parameterId = 0x7Bu,
    )

    /**
     * Diesel Particulate filter (DPF) temperature.
     */
    data object DIESEL_PARTICULATE_FILTER_TEMPERATURE :
        DataType<DieselParticulateFilterTemperature>(
            parameterId = 0x7Cu,
            expectedBytes = 9,
            responseParser = {
                DieselParticulateFilterTemperature.fromObdValue(it)
            },
        )

    /**
     * NOx NTE (Not-To-Exceed) control area status.
     */
    data object NOX_NTE_CONTROL_AREA_STATUS : DataType<Nothing>(
        parameterId = 0x7Du,
    )

    /**
     * PM NTE (Not-To-Exceed) control area status.
     */
    data object PM_NTE_CONTROL_AREA_STATUS : DataType<Nothing>(
        parameterId = 0x7Eu,
    )

    /**
     * Engine run time.
     */
    data object ENGINE_RUN_TIME : DataType<Nothing>(
        parameterId = 0x7Fu,
    )

    /**
     * PIDs supported [$81 - $A0].
     */
    data object PID_SUPPORTED_81_A0 : DataType<SupportedParameterIds>(
        parameterId = 0x80u,
        expectedBytes = 4,
        responseParser = getSupportedParameterIdsParser(0x80u),
    )

    /**
     * Engine run time for Auxiliary Emissions Control Device (AECD) #1-#5.
     */
    data object ENGINE_RUN_TIME_FOR_AECD_1_5 : DataType<Nothing>(
        parameterId = 0x81u,
    )

    /**
     * Engine run time for Auxiliary Emissions Control Device (AECD) #6-#10.
     */
    data object ENGINE_RUN_TIME_FOR_AECD_6_10 : DataType<Nothing>(
        parameterId = 0x82u,
    )

    /**
     * NOx sensor.
     */
    data object NOX_SENSOR : DataType<Nothing>(
        parameterId = 0x83u,
    )

    /**
     * Manifold surface temperature.
     */
    data object MANIFOLD_SURFACE_TEMPERATURE : DataType<Nothing>(
        parameterId = 0x84u,
    )

    /**
     * NOx reagent system.
     */
    data object NOX_REAGENT_SYSTEM : DataType<Nothing>(
        parameterId = 0x85u,
    )

    /**
     * Particulate matter (PM) sensor.
     */
    data object PARTICULATE_MATTER_SENSOR : DataType<Nothing>(
        parameterId = 0x86u,
    )

    /**
     * Intake manifold absolute pressure.
     */
    data object INTAKE_MANIFOLD_ABSOLUTE_PRESSURE_TWO_BANKS : DataType<Nothing>(
        parameterId = 0x87u,
        expectedBytes = 5,
    )

    /**
     * SCR Induce System.
     */
    data object SCR_INDUCE_SYSTEM : DataType<Nothing>(
        parameterId = 0x88u,
    )

    /**
     * Engine run time for Auxiliary Emissions Control Device (AECD) #11-#15.
     */
    data object RUN_TIME_FOR_AECD_11_15 : DataType<Nothing>(
        parameterId = 0x89u,
    )

    /**
     * Engine run time for Auxiliary Emissions Control Device (AECD) #16-#20.
     */
    data object RUN_TIME_FOR_AECD_16_20 : DataType<Nothing>(
        parameterId = 0x8Au,
    )

    /**
     * Diesel Aftertreatment.
     */
    data object DIESEL_AFTERTREATMENT : DataType<Nothing>(
        parameterId = 0x8Bu,
    )

    /**
     * O2 Sensor (Wide Range).
     */
    data object O2_SENSOR_WIDE_RANGE : DataType<Nothing>(
        parameterId = 0x8Cu,
    )

    /**
     * Throttle Position G.
     */
    data object THROTTLE_POSITION_G : DataType<Nothing>(
        parameterId = 0x8Du,
    )

    /**
     * Engine Friction - Percent Torque.
     */
    data object ENGINE_FRICTION_PERCENT_TORQUE : DataType<Nothing>(
        parameterId = 0x8Eu,
    )

    /**
     * PM Sensor Bank 1 & 2.
     */
    data object PM_SENSOR_BANK_1_2 : DataType<Nothing>(
        parameterId = 0x8Fu,
    )

    /**
     * WWH-OBD Vehicle OBD System Information.
     */
    data object WWH_OBD_VEHICLE_OBD_SYSTEM_INFORMATION : DataType<Nothing>(
        parameterId = 0x90u,
    )

    /**
     * WWH-OBD Vehicle OBD System Information.
     */
    data object WWH_OBD_VEHICLE_OBD_SYSTEM_INFORMATION_2 : DataType<Nothing>(
        parameterId = 0x91u,
    ) // TODO: What?

    /**
     * Fuel System Control.
     */
    data object FUEL_SYSTEM_CONTROL : DataType<Nothing>(
        parameterId = 0x92u,
    )

    /**
     * WWH-OBD Vehicle OBD Counters support.
     */
    data object WWH_OBD_VEHICLE_OBD_COUNTERS_SUPPORT : DataType<Nothing>(
        parameterId = 0x93u,
    )

    /**
     * NOx Warning And Inducement System.
     */
    data object NOX_WARNING_AND_INDUCEMENT_SYSTEM : DataType<Nothing>(
        parameterId = 0x94u,
    )

    /**
     * Exhaust Gas Temperature Sensor.
     */
    data object EXHAUST_GAS_TEMPERATURE_SENSOR : DataType<Nothing>(
        parameterId = 0x98u,
    )

    /**
     * Exhaust Gas Temperature Sensor.
     */
    data object EXHAUST_GAS_TEMPERATURE_SENSOR_2 : DataType<Nothing>(
        parameterId = 0x99u,
    ) // TODO: What?

    /**
     * Hybrid/EV Vehicle System Data, Battery, Voltage.
     */
    data object HYBRID_EV_VEHICLE_SYSTEM_DATA_BATTERY_VOLTAGE : DataType<Nothing>(
        parameterId = 0x9Au,
    )

    /**
     * Diesel Exhaust Fluid Sensor Data.
     */
    data object DIESEL_EXHAUST_FLUID_SENSOR_DATA : DataType<Nothing>(
        parameterId = 0x9Bu,
    )

    /**
     * O2 Sensor Data.
     */
    data object O2_SENSOR_DATA : DataType<Nothing>(
        parameterId = 0x9Cu,
    )

    /**
     * Engine Fuel Rate.
     */
    data object ENGINE_FUEL_RATE_2 : DataType<Nothing>(
        parameterId = 0x9Du,
    ) // TODO: Duplicate

    /**
     * Engine Exhaust Flow Rate.
     */
    data object ENGINE_EXHAUST_FLOW_RATE : DataType<Nothing>(
        parameterId = 0x9Eu,
    )

    /**
     * Fuel System Percentage Use.
     */
    data object FUEL_SYSTEM_PERCENTAGE_USE : DataType<Nothing>(
        parameterId = 0x9Fu,
    )

    /**
     * PIDs supported [$A1 - $C0].
     */
    data object PID_SUPPORTED_A1_C0 : DataType<SupportedParameterIds>(
        parameterId = 0xA0u,
        expectedBytes = 4,
        responseParser = getSupportedParameterIdsParser(0xA0u),
    )

    /**
     * NOx Sensor Corrected Data.
     */
    data object NOX_SENSOR_CORRECTED_DATA : DataType<Nothing>(
        parameterId = 0xA1u,
    )

    /**
     * Cylinder Fuel Rate.
     */
    data object CYLINDER_FUEL_RATE : DataType<Nothing>(
        parameterId = 0xA2u,
    )

    /**
     * Evap System Vapor Pressure.
     */
    data object EVAP_SYSTEM_VAPOR_PRESSURE_2 : DataType<Nothing>(
        parameterId = 0xA3u,
    ) // TODO: Duplicate

    /**
     * Transmission Actual Gear.
     */
    data object TRANSMISSION_ACTUAL_GEAR : DataType<Nothing>(
        parameterId = 0xA4u,
    )

    /**
     * Commanded Diesel Exhaust Fluid Dosing.
     */
    data object COMMANDED_DIESEL_EXHAUST_FLUID_DOSING : DataType<Nothing>(
        parameterId = 0xA5u,
    )

    /**
     * Odometer.
     */
    data object ODOMETER : DataType<Length>(
        parameterId = 0xA6u,
        expectedBytes = 4,
        responseParser = {
            it.toUInt().toLong()
                .div(10f)
                .kilometers
        }
    )

    /**
     * NOx Sensor Concentration Sensors 3 and 4.
     */
    data object NOX_SENSOR_CONCENTRATION_SENSORS_3_4 : DataType<Nothing>(
        parameterId = 0xA7u,
    )

    /**
     * NOx Sensor Corrected Concentration Sensors 3 and 4.
     */
    data object NOX_SENSOR_CORRECTED_CONCENTRATION_SENSORS_3_4 : DataType<Nothing>(
        parameterId = 0xA8u,
    )

    /**
     * ABS Disable Switch State.
     */
    data object ABS_DISABLE_SWITCH_STATE : DataType<Nothing>(
        parameterId = 0xA9u,
    )

    /**
     * PIDs supported [$C1 - $E0].
     */
    data object PID_SUPPORTED_C1_E0 : DataType<SupportedParameterIds>(
        parameterId = 0xC0u,
        expectedBytes = 4,
        responseParser = getSupportedParameterIdsParser(0xC0u),
    )

    /**
     * Fuel Level Input A/B.
     */
    data object FUEL_LEVEL_INPUT_A_B : DataType<Nothing>(
        parameterId = 0xC3u,
    )

    /**
     * Exhaust Particulate Control System Diagnostic Time/Count.
     */
    data object EXHAUST_PARTICULATE_CONTROL_SYSTEM_DIAGNOSTIC_TIME_COUNT : DataType<Nothing>(
        parameterId = 0xC4u,
    )

    /**
     * Fuel Pressure A and B.
     */
    data object FUEL_PRESSURE_A_B : DataType<Nothing>(
        parameterId = 0xC5u,
    )

    /**
     * - Byte 1 - Particulate control - driver inducement system status
     * - Byte 2,3 - Removal or block of the particulate aftertreatment system counter
     * - Byte 4,5 - Liquid regent injection system (e.g. fuel-borne catalyst) failure counter
     * - Byte 6,7 - Malfunction of Particulate control monitoring system counter
     */
    data object PARTICULATE_CONTROL_SYSTEM_DIAGNOSTIC_TIME_COUNT : DataType<Nothing>(
        parameterId = 0xC6u,
    ) // TODO: Check

    /**
     * Distance Since Reflash or Module Replacement.
     */
    data object DISTANCE_SINCE_REFLASH_OR_MODULE_REPLACEMENT : DataType<Nothing>(
        parameterId = 0xC7u,
    )

    /**
     * NOx Control Diagnostic (NCD) and Particulate Control Diagnostic (PCD) Warning Lamp status.
     */
    data object NCD_AND_PCD_WARNING_LAMP_STATUS : DataType<Nothing>(
        parameterId = 0xC8u,
    )

    companion object {
        val all by lazy {
            listOf<DataType<*>>(
                PID_SUPPORTED_01_20,
                MONITOR_STATUS_SINCE_DTC_CLEARED,
                DTC_THAT_CAUSED_FREEZE_FRAME_TO_BE_STORED,
                FUEL_SYSTEM_STATUS,
                CALCULATED_ENGINE_LOAD,
                ENGINE_COOLANT_TEMPERATURE,
                SHORT_TERM_FUEL_TRIM_1,
                LONG_TERM_FUEL_TRIM_1,
                SHORT_TERM_FUEL_TRIM_2,
                LONG_TERM_FUEL_TRIM_2,
                FUEL_PRESSURE,
                INTAKE_MANIFOLD_ABSOLUTE_PRESSURE,
                ENGINE_SPEED,
                VEHICLE_SPEED,
                TIMING_ADVANCE,
                INTAKE_AIR_TEMPERATURE,
                MAF_AIR_FLOW_RATE,
                THROTTLE_POSITION,
                COMMANDED_SECONDARY_AIR_STATUS,
                OXYGEN_SENSORS_PRESENT_2_BANKS,
                OXYGEN_SENSOR_1_V_STFT,
                OXYGEN_SENSOR_2_V_STFT,
                OXYGEN_SENSOR_3_V_STFT,
                OXYGEN_SENSOR_4_V_STFT,
                OXYGEN_SENSOR_5_V_STFT,
                OXYGEN_SENSOR_6_V_STFT,
                OXYGEN_SENSOR_7_V_STFT,
                OXYGEN_SENSOR_8_V_STFT,
                OBD_STANDARDS_CONFORMING_TO,
                OXYGEN_SENSORS_PRESENT_4_BANKS,
                AUXILIARY_INPUT_STATUS,
                RUN_TIME_SINCE_ENGINE_START,
                PID_SUPPORTED_21_40,
                DISTANCE_TRAVELED_WITH_MIL_ON,
                FUEL_RAIL_PRESSURE,
                FUEL_RAIL_GAUGE_PRESSURE,
                OXYGEN_SENSOR_1_AF_V,
                OXYGEN_SENSOR_2_AF_V,
                OXYGEN_SENSOR_3_AF_V,
                OXYGEN_SENSOR_4_AF_V,
                OXYGEN_SENSOR_5_AF_V,
                OXYGEN_SENSOR_6_AF_V,
                OXYGEN_SENSOR_7_AF_V,
                OXYGEN_SENSOR_8_AF_V,
                COMMANDED_EGR,
                EGR_ERROR,
                COMMANDED_EVAPORATIVE_PURGE,
                FUEL_TANK_LEVEL_INPUT,
                WARM_UPS_SINCE_CODES_CLEARED,
                DISTANCE_TRAVELED_SINCE_CODES_CLEARED,
                EVAPORATION_SYSTEM_VAPOR_PRESSURE,
                ABSOLUTE_BAROMETRIC_PRESSURE,
                OXYGEN_SENSOR_1_AF_A,
                OXYGEN_SENSOR_2_AF_A,
                OXYGEN_SENSOR_3_AF_A,
                OXYGEN_SENSOR_4_AF_A,
                OXYGEN_SENSOR_5_AF_A,
                OXYGEN_SENSOR_6_AF_A,
                OXYGEN_SENSOR_7_AF_A,
                OXYGEN_SENSOR_8_AF_A,
                CATALYST_TEMPERATURE_BANK_1_SENSOR_1,
                CATALYST_TEMPERATURE_BANK_2_SENSOR_1,
                CATALYST_TEMPERATURE_BANK_1_SENSOR_2,
                CATALYST_TEMPERATURE_BANK_2_SENSOR_2,
                PID_SUPPORTED_41_60,
                CURRENT_DRIVE_CYCLE_MONITOR_STATUS,
                CONTROL_MODULE_VOLTAGE,
                ABSOLUTE_LOAD_VALUE,
                COMMANDED_AIR_FUEL_EQUIVALENCE_RATIO,
                RELATIVE_THROTTLE_POSITION,
                AMBIENT_AIR_TEMPERATURE,
                ABSOLUTE_THROTTLE_POSITION_B,
                ABSOLUTE_THROTTLE_POSITION_C,
                ACCELERATOR_PEDAL_POSITION_D,
                ACCELERATOR_PEDAL_POSITION_E,
                ACCELERATOR_PEDAL_POSITION_F,
                COMMANDED_THROTTLE_ACTUATOR,
                TIME_RUN_WITH_MIL_ON,
                TIME_SINCE_TROUBLE_CODES_CLEARED,
                EXTERNAL_TEST_EQUIPMENT_CONFIGURATION_1,
                EXTERNAL_TEST_EQUIPMENT_CONFIGURATION_2,
                FUEL_TYPE,
                ETHANOL_FUEL_PERCENTAGE,
                ABSOLUTE_EVAP_SYSTEM_VAPOR_PRESSURE,
                EVAP_SYSTEM_VAPOR_PRESSURE,
                SHORT_TERM_SECONDARY_OXYGEN_SENSOR_TRIM_1_3,
                LONG_TERM_SECONDARY_OXYGEN_SENSOR_TRIM_1_3,
                SHORT_TERM_SECONDARY_OXYGEN_SENSOR_TRIM_2_4,
                LONG_TERM_SECONDARY_OXYGEN_SENSOR_TRIM_2_4,
                FUEL_RAIL_ABSOLUTE_PRESSURE,
                RELATIVE_ACCELERATOR_PEDAL_POSITION,
                HYBRID_BATTERY_PACK_REMAINING_LIFE,
                ENGINE_OIL_TEMPERATURE,
                FUEL_INJECTION_TIMING,
                ENGINE_FUEL_RATE,
                DESIGNED_EMISSION_REQUIREMENTS,
                PID_SUPPORTED_61_80,
                DRIVER_DEMAND_ENGINE_PERCENT_TORQUE,
                ACTUAL_ENGINE_PERCENT_TORQUE,
                ENGINE_REFERENCE_TORQUE,
                ENGINE_PERCENT_TORQUE_DATA,
                AUXILIARY_INPUT_OUTPUT_SUPPORTED,
                MASS_AIR_FLOW_SENSOR,
                ENGINE_COOLANT_TEMPERATURE_2_SENSORS,
                INTAKE_AIR_TEMPERATURE_SENSOR,
                EGR_ACTUAL_COMMANDED_ERROR,
                COMMANDED_DIESEL_INTAKE_AIR_FLOW_CONTROL_AND_RELATIVE_INTAKE_AIR_FLOW_POSITION,
                EXHAUST_GAS_RECIRCULATION_TEMPERATURE,
                COMMANDED_THROTTLE_ACTUATOR_CONTROL_AND_RELATIVE_THROTTLE_POSITION,
                FUEL_PRESSURE_CONTROL_SYSTEM,
                INJECTION_PRESSURE_CONTROL_SYSTEM,
                TURBOCHARGER_COMPRESSOR_INLET_PRESSURE,
                BOOST_PRESSURE_CONTROL,
                VARIABLE_GEOMETRY_TURBO_CONTROL,
                WASTEGATE_CONTROL,
                EXHAUST_PRESSURE,
                TURBOCHARGER_RPM,
                TURBOCHARGER_A_TEMPERATURE,
                TURBOCHARGER_B_TEMPERATURE,
                CHARGE_AIR_COOLER_TEMPERATURE,
                EXHAUST_GAS_TEMPERATURE_BANK_1,
                EXHAUST_GAS_TEMPERATURE_BANK_2,
                DIESEL_PARTICULATE_FILTER_DIFFERENTIAL_PRESSURE_BANK_1,
                DIESEL_PARTICULATE_FILTER_DIFFERENTIAL_PRESSURE_BANK_2,
                DIESEL_PARTICULATE_FILTER_TEMPERATURE,
                NOX_NTE_CONTROL_AREA_STATUS,
                PM_NTE_CONTROL_AREA_STATUS,
                ENGINE_RUN_TIME,
                PID_SUPPORTED_81_A0,
                ENGINE_RUN_TIME_FOR_AECD_1_5,
                ENGINE_RUN_TIME_FOR_AECD_6_10,
                NOX_SENSOR,
                MANIFOLD_SURFACE_TEMPERATURE,
                NOX_REAGENT_SYSTEM,
                PARTICULATE_MATTER_SENSOR,
                INTAKE_MANIFOLD_ABSOLUTE_PRESSURE_TWO_BANKS,
                SCR_INDUCE_SYSTEM,
                RUN_TIME_FOR_AECD_11_15,
                RUN_TIME_FOR_AECD_16_20,
                DIESEL_AFTERTREATMENT,
                O2_SENSOR_WIDE_RANGE,
                THROTTLE_POSITION_G,
                ENGINE_FRICTION_PERCENT_TORQUE,
                PM_SENSOR_BANK_1_2,
                WWH_OBD_VEHICLE_OBD_SYSTEM_INFORMATION,
                WWH_OBD_VEHICLE_OBD_SYSTEM_INFORMATION_2,
                FUEL_SYSTEM_CONTROL,
                WWH_OBD_VEHICLE_OBD_COUNTERS_SUPPORT,
                NOX_WARNING_AND_INDUCEMENT_SYSTEM,
                EXHAUST_GAS_TEMPERATURE_SENSOR,
                EXHAUST_GAS_TEMPERATURE_SENSOR_2,
                HYBRID_EV_VEHICLE_SYSTEM_DATA_BATTERY_VOLTAGE,
                DIESEL_EXHAUST_FLUID_SENSOR_DATA,
                O2_SENSOR_DATA,
                ENGINE_FUEL_RATE_2,
                ENGINE_EXHAUST_FLOW_RATE,
                FUEL_SYSTEM_PERCENTAGE_USE,
                PID_SUPPORTED_A1_C0,
                NOX_SENSOR_CORRECTED_DATA,
                CYLINDER_FUEL_RATE,
                EVAP_SYSTEM_VAPOR_PRESSURE_2,
                TRANSMISSION_ACTUAL_GEAR,
                COMMANDED_DIESEL_EXHAUST_FLUID_DOSING,
                ODOMETER,
                NOX_SENSOR_CONCENTRATION_SENSORS_3_4,
                NOX_SENSOR_CORRECTED_CONCENTRATION_SENSORS_3_4,
                ABS_DISABLE_SWITCH_STATE,
                PID_SUPPORTED_C1_E0,
                FUEL_LEVEL_INPUT_A_B,
                EXHAUST_PARTICULATE_CONTROL_SYSTEM_DIAGNOSTIC_TIME_COUNT,
                FUEL_PRESSURE_A_B,
                PARTICULATE_CONTROL_SYSTEM_DIAGNOSTIC_TIME_COUNT,
                DISTANCE_SINCE_REFLASH_OR_MODULE_REPLACEMENT,
                NCD_AND_PCD_WARNING_LAMP_STATUS,
            ).associateBy(DataType<*>::parameterId)
        }

        /**
         * Get a [DataType] from a [parameterId].
         */
        fun fromParameterId(parameterId: UByte) = all[parameterId]

        private fun getSupportedParameterIdsParser(offset: UByte) = { response: UByteArray ->
            SupportedParameterIds.fromObdValue(response.toUInt(), offset)
        }

        private val monitorStatusParser = { response: UByteArray ->
            MonitorStatus.fromObdValue(response.toUInt())
        }

        private val fuelTrimParser = { response: UByteArray ->
            response[0].toInt().div(128f)
                .minus(1)
                .fraction
        }

        private val oxygenSensorVStftParser = { response: UByteArray ->
            val v = response[0].toInt().div(200f)
            val stft = response[1].takeIf { it != 0xFFu.toUByte() }?.toInt()?.div(128f)
                ?.minus(1)
                ?.fraction

            v to stft
        }

        private val oxygenSensorAfVParser = { response: UByteArray ->
            val (afBytes, vBytes) = response.windowed(2)

            val af = afBytes.toUShort().toInt()
                .times(2f / 0xFFFF)
            val v = vBytes.toUShort().toInt()
                .times(8f / 0xFFFF)
                .volts

            af to v
        }

        private val oxygenSensorAfAParser = { response: UByteArray ->
            val (afBytes, aBytes) = response.windowed(2)

            val af = afBytes.toUShort().toInt()
                .times(2f / 0xFFFF)
            val a = aBytes.toUShort().toInt()
                .div(256)
                .minus(128)
                .milliamperes

            af to a
        }

        private val catalystTemperatureParser = { response: UByteArray ->
            response.toUShort().toInt()
                .div(10f)
                .minus(40)
                .celsius
        }

        private val positionParser = { response: UByteArray ->
            response[0].toInt().div(255f).fraction
        }

        private val secondaryOxygenSensorTrimTwoBanksParser = { response: UByteArray ->
            val firstBankFuelTrim = response[0].toInt()
                .div(128f)
                .minus(1)
                .fraction
            val secondBankFuelTrim = response[1].toInt()
                .div(128f)
                .minus(1)
                .fraction

            firstBankFuelTrim to secondBankFuelTrim
        }
    }
}
