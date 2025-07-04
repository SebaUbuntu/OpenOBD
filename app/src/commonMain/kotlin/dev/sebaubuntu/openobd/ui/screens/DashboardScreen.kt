/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.sebaubuntu.openobd.ext.valueToPercentage
import dev.sebaubuntu.openobd.models.Dashboard
import dev.sebaubuntu.openobd.repositories.ConnectionStatusRepository
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.repositories.ObdRepository
import dev.sebaubuntu.openobd.ui.composables.ConnectionGatedComposable
import dev.sebaubuntu.openobd.ui.composables.Gauge
import dev.sebaubuntu.openobd.viewmodels.DashboardViewModel

/**
 * Temperature and fuel gauges.
 */
private const val SECONDARY_GAUGE_PERCENTAGE = 0.15f

/**
 * Center layout.
 */
private const val PRIMARY_LAYOUT_PERCENTAGE = 0.75f

/**
 * RPM and speed gauges.
 */
private const val MAIN_GAUGE_PERCENTAGE = 0.4f

/**
 * Display long side.
 */
private const val DISPLAY_LONG_SIDE_PERCENTAGE = 0.2f

/**
 * Display short side.
 */
private const val DISPLAY_SHORT_SIDE_PERCENTAGE = 0.4f

/**
 * Cluster like screen.
 * Inspired by the Peugeot 208 MK1 cluster.
 */
@Composable
fun DashboardScreen(
    paddingValues: PaddingValues,
    connectionStatusRepository: ConnectionStatusRepository,
    obdRepository: ObdRepository,
) {
    ConnectionGatedComposable(
        connectionStatusRepository = connectionStatusRepository,
        paddingValues = paddingValues,
    ) {
        val dashboardViewModel = viewModel {
            DashboardViewModel(
                obdRepository = obdRepository,
            )
        }

        val dashboard by dashboardViewModel.dashboard.collectAsStateWithLifecycle()

        DashboardScreen(
            paddingValues = paddingValues,
            dashboard = dashboard,
        )
    }
}

@Composable
private fun DashboardScreen(
    paddingValues: PaddingValues,
    dashboard: Dashboard,
) {
    BoxWithConstraints(
        modifier = Modifier.padding(paddingValues),
    ) {
        val isLandscape = maxWidth > maxHeight

        // Coolant temperature gauge
        val coolantTemperatureRange = remember {
            50..140 step 20
        }
        val coolantTemperatureLabels = remember {
            coolantTemperatureRange.associate { i ->
                coolantTemperatureRange.valueToPercentage(i) to "$i"
            }
        }

        Gauge(
            value = {
                coolantTemperatureRange.valueToPercentage(dashboard.coolantTemperatureCelsius)
            },
            labels = coolantTemperatureLabels,
            labelsTextSize = 12.sp,
            modifier = Modifier
                .fillMaxLongSide(
                    isLandscape = isLandscape,
                    fraction = SECONDARY_GAUGE_PERCENTAGE,
                )
                .fillMaxShortSide(
                    isLandscape = isLandscape
                )
                .align(
                    when (isLandscape) {
                        true -> Alignment.CenterStart
                        false -> Alignment.TopCenter
                    }
                ),
            rotationAngle = when (isLandscape) {
                true -> 270f
                false -> 0f
            },
            startAngle = 210f,
            sweepAngle = 120f,
            invert = false,
        )

        Box(
            modifier = Modifier
                .fillMaxLongSide(
                    isLandscape = isLandscape,
                    fraction = PRIMARY_LAYOUT_PERCENTAGE,
                )
                .fillMaxShortSide(
                    isLandscape = isLandscape
                )
                .align(Alignment.Center)
        ) {
            // RPM gauge
            val rpmLabels = remember {
                val range = (0..(dashboard.maxRpm / 1000))

                range.associate { i ->
                    (i.toFloat() / range.last.toFloat()) to "$i"
                }
            }

            Gauge(
                value = { dashboard.rpm.toFloat() / dashboard.maxRpm.toFloat() },
                labels = rpmLabels,
                modifier = Modifier
                    .fillMaxLongSide(
                        isLandscape = isLandscape,
                        fraction = MAIN_GAUGE_PERCENTAGE,
                    )
                    .fillMaxShortSide(
                        isLandscape = isLandscape
                    )
                    .align(
                        when (isLandscape) {
                            true -> Alignment.CenterStart
                            false -> Alignment.TopCenter
                        }
                    ),
            )

            // Display
            DashboardDisplay(
                speedKph = dashboard.speedKph,
                odometer = dashboard.odometer,
                modifier = Modifier
                    .fillMaxLongSide(
                        isLandscape = isLandscape,
                        fraction = DISPLAY_LONG_SIDE_PERCENTAGE,
                    )
                    .fillMaxShortSide(
                        isLandscape = isLandscape,
                        fraction = DISPLAY_SHORT_SIDE_PERCENTAGE,
                    )
                    .align(Alignment.Center)
            )

            // Speed gauge
            val speedLabels = remember {
                val range = (10..dashboard.maxSpeedKph step 20)

                range.toMutableList().associate { i ->
                    (i.toFloat() / dashboard.maxSpeedKph.toFloat()) to "$i"
                }
            }

            Gauge(
                value = { dashboard.speedKph.toFloat() / dashboard.maxSpeedKph.toFloat() },
                labels = speedLabels,
                modifier = Modifier
                    .fillMaxLongSide(
                        isLandscape = isLandscape,
                        fraction = MAIN_GAUGE_PERCENTAGE,
                    )
                    .fillMaxShortSide(
                        isLandscape = isLandscape
                    )
                    .align(
                        when (isLandscape) {
                            true -> Alignment.CenterEnd
                            false -> Alignment.BottomCenter
                        }
                    ),
            )
        }

        // Fuel gauge
        Gauge(
            value = { dashboard.fuelLevel },
            labels = mapOf(
                0f to "0",
                0.5f to "1/2",
                1f to "1",
            ),
            labelsTextSize = 12.sp,
            modifier = Modifier
                .fillMaxLongSide(
                    isLandscape = isLandscape,
                    fraction = SECONDARY_GAUGE_PERCENTAGE,
                )
                .fillMaxShortSide(
                    isLandscape = isLandscape
                )
                .align(
                    when (isLandscape) {
                        true -> Alignment.CenterEnd
                        false -> Alignment.BottomCenter
                    }
                ),
            rotationAngle = when (isLandscape) {
                true -> 90f
                false -> 180f
            },
            startAngle = 210f,
            sweepAngle = 120f,
            invert = true,
        )
    }
}

@Composable
private fun DashboardDisplay(
    speedKph: Int,
    odometer: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(Color(0xFF0F0F11)),
    ) {
        Text(
            text = "$speedKph km/h",
            modifier = Modifier.align(Alignment.TopEnd),
            color = Color.White,
            fontFamily = FontFamily.Monospace,
        )

        Text(
            "$odometer km",
            modifier = Modifier.align(Alignment.BottomStart),
            color = Color.White,
            fontFamily = FontFamily.Monospace,
        )
    }
}

private fun Modifier.fillMaxLongSide(
    @androidx.annotation.FloatRange(from = 0.0, to = 1.0) fraction: Float = 1f,
    isLandscape: Boolean,
) = when (isLandscape) {
    true -> fillMaxWidth(fraction)
    false -> fillMaxHeight(fraction)
}

private fun Modifier.fillMaxShortSide(
    @androidx.annotation.FloatRange(from = 0.0, to = 1.0) fraction: Float = 1f,
    isLandscape: Boolean,
) = when (isLandscape) {
    true -> fillMaxHeight(fraction)
    false -> fillMaxWidth(fraction)
}
