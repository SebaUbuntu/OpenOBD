/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebaubuntu.openobd.app.repositories.Elm327Repository
import dev.sebaubuntu.openobd.core.models.Error
import dev.sebaubuntu.openobd.core.models.Result
import dev.sebaubuntu.openobd.elm327.commands.RawCommand
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TerminalViewModel(
    private val elm327Repository: Elm327Repository,
) : ViewModel() {
    data class Exchange(
        val command: String,
        val response: Result<List<String>, Error>,
    )

    private val exchangesList = mutableListOf<Exchange>()

    private val _exchanges = MutableStateFlow(exchangesList.toList())
    val exchanges = _exchanges.asStateFlow()

    fun sendCommand(command: String) = viewModelScope.launch(Dispatchers.IO) {
        if (command.isBlank()) {
            return@launch
        }

        val rawCommand = RawCommand(command)

        val result = elm327Repository.executeCommand(rawCommand)

        addExchange(command, result)
    }

    private fun addExchange(command: String, response: Result<List<String>, Error>) {
        exchangesList.add(
            Exchange(
                command = command,
                response = response,
            )
        )
        _exchanges.value = exchangesList.toList()
    }
}
