/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.app.repositories.Elm327Repository
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.network.obd2.commands.ClearDiagnosticTroubleCodesCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class ClearDiagnosticTroubleCodesViewModel(
    private val elm327Repository: Elm327Repository,
) : ViewModel() {
    sealed interface UiState {
        data object Idle : UiState
        data object Clearing : UiState
        data object Success : UiState
        data object Failure : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun clearCodes() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_uiState.value == UiState.Clearing) {
                return@launch
            }

            _uiState.value = UiState.Clearing

            val result = elm327Repository.executeCommand(ClearDiagnosticTroubleCodesCommand)

            _uiState.value = when (result) {
                is Result.Success -> UiState.Success
                is Result.Failure -> UiState.Failure
            }
        }
    }
}
