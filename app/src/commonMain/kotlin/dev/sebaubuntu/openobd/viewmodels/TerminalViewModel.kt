/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.elm327.commands.RawCommand
import dev.sebaubuntu.openobd.repositories.ObdRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TerminalViewModel(
    private val obdRepository: ObdRepository,
) : ViewModel() {
    data class Exchange(
        val command: String,
        val response: Result<String, Error>,
    )

    private val exchangesList = mutableListOf<Exchange>()

    private val _exchanges = MutableStateFlow(exchangesList.toList())
    val exchanges = _exchanges.asStateFlow()

    fun sendCommand(command: String) = viewModelScope.launch(Dispatchers.IO) {
        if (command.isBlank()) {
            return@launch
        }

        val rawCommand = RawCommand(command)

        val result = obdRepository.executeCommand(rawCommand)

        addExchange(command, result)
    }

    private fun addExchange(command: String, response: Result<String, Error>) {
        exchangesList.add(
            Exchange(
                command = command,
                response = response,
            )
        )
        _exchanges.value = exchangesList.toList()
    }
}
