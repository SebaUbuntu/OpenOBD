/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import dev.sebaubuntu.openobd.models.Dashboard
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Dashboard view model.
 */
class DashboardViewModel : ViewModel() {
    val dashboard = MutableStateFlow(Dashboard.DEFAULT)
}
