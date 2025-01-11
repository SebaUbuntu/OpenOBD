/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.models

/**
 * Measure systems.
 */
enum class MeasurementSystem {
    /**
     * Metric system.
     */
    METRIC,

    /**
     * Imperial system (US).
     */
    IMPERIAL_US,

    /**
     * Imperial system (UK).
     */
    IMPERIAL_UK,
}
