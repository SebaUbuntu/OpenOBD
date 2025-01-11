/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.utils

import dev.sebaubuntu.openobd.app.models.MeasurementSystem
import dev.sebaubuntu.openobd.core.models.value.Speed
import dev.sebaubuntu.openobd.core.models.value.Temperature

object LocaleUtils {
    /**
     * List of two-code countries that use Fahrenheit as their temperature unit.
     *
     * [Source](https://worldpopulationreview.com/country-rankings/countries-that-use-fahrenheit)
     */
    private val fahrenheitCountries = setOf(
        "AG",
        "AI",
        "BM",
        "BS",
        "BZ",
        "FM",
        "GU",
        "KN",
        "KY",
        "LR",
        "MH",
        "MP",
        "MS",
        "PR",
        "PW",
        "US",
        "VG",
        "VI",
        "WS",
    )

    /**
     * List of two-code countries that use miles per hour as their speed unit.
     *
     * [Source](https://www.rhinocarhire.com/Drive-Smart-Blog/Which-Countries-use-MPH-or-KPH.aspx)
     */
    private val mphCountries = setOf(
        "AG",
        "AI",
        "BB",
        "GB",
        "GG",
        "GU",
        "IM",
        "JE",
        "KY",
        "PR",
        "US",
    )

    /**
     * Get the [MeasurementSystem] for the specified country.
     *
     * [Source](https://worldpopulationreview.com/country-rankings/countries-that-use-imperial)
     */
    fun measurementSystemForCountry(country: String) = when (country.uppercase()) {
        "GB" -> MeasurementSystem.IMPERIAL_UK
        "LR", "MM", "US" -> MeasurementSystem.IMPERIAL_US
        else -> MeasurementSystem.METRIC
    }

    /**
     * Get the [Speed.Unit] for the specified country.
     */
    fun speedUnitForCountry(country: String) = when (country.uppercase()) {
        in mphCountries -> Speed.Unit.MILE_PER_HOUR
        else -> Speed.Unit.KILOMETER_PER_HOUR
    }

    /**
     * Get the [Temperature.Unit] for the specified country.
     */
    fun temperatureUnitForCountry(country: String) = when (country.uppercase()) {
        in fahrenheitCountries -> Temperature.Unit.FAHRENHEIT
        else -> Temperature.Unit.CELSIUS
    }
}
