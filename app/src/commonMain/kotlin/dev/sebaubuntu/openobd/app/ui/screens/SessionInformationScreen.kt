/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sebaubuntu.openobd.app.models.SessionInformation
import dev.sebaubuntu.openobd.app.ui.composables.ConnectionGatedComposable
import dev.sebaubuntu.openobd.app.ui.composables.FlowResultComposable
import dev.sebaubuntu.openobd.app.viewmodels.SessionInformationViewModel
import openobd.app.generated.resources.Res
import openobd.app.generated.resources.session_information_device_description
import openobd.app.generated.resources.session_information_device_identifier
import openobd.app.generated.resources.session_information_ignition
import openobd.app.generated.resources.session_information_input_voltage
import openobd.app.generated.resources.session_information_obd_protocol
import openobd.app.generated.resources.session_information_obd_protocol_description
import openobd.app.generated.resources.session_information_version_id
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SessionInformationScreen(
    paddingValues: PaddingValues,
) {
    ConnectionGatedComposable(
        paddingValues = paddingValues,
    ) {
        val sessionInformationViewModel = koinViewModel<SessionInformationViewModel>()

        val sessionInformation by sessionInformationViewModel.sessionInformation.collectAsStateWithLifecycle()

        FlowResultComposable(
            flowResult = sessionInformation,
            paddingValues = paddingValues,
        ) {
            SessionInformationScreen(
                paddingValues = paddingValues,
                sessionInformation = it,
            )
        }
    }
}

@Composable
private fun SessionInformationScreen(
    paddingValues: PaddingValues,
    sessionInformation: SessionInformation,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
            .verticalScroll(rememberScrollState()),
    ) {
        sessionInformation.deviceDescription?.let {
            InformationListItem(
                titleStringResource = Res.string.session_information_device_description,
                information = it,
            )
        }
        sessionInformation.deviceIdentifier?.let {
            InformationListItem(
                titleStringResource = Res.string.session_information_device_identifier,
                information = it,
            )
        }
        sessionInformation.versionId?.let {
            InformationListItem(
                titleStringResource = Res.string.session_information_version_id,
                information = it,
            )
        }
        sessionInformation.inputVoltage?.let {
            InformationListItem(
                titleStringResource = Res.string.session_information_input_voltage,
                information = it.toString(),
            )
        }
        sessionInformation.obdProtocol?.let {
            InformationListItem(
                titleStringResource = Res.string.session_information_obd_protocol,
                information = it.toString(),
            )
        }
        sessionInformation.obdProtocolDescription?.let {
            InformationListItem(
                titleStringResource = Res.string.session_information_obd_protocol_description,
                information = it,
            )
        }
        sessionInformation.ignition?.let {
            InformationListItem(
                titleStringResource = Res.string.session_information_ignition,
                information = it.toString(),
            )
        }
    }
}

@Composable
private fun InformationListItem(
    titleStringResource: StringResource,
    information: Any,
) {
    ListItem(
        headlineContent = {
            Text(
                text = stringResource(titleStringResource),
            )
        },
        modifier = Modifier,
        supportingContent = {
            Text(
                text = information.toString(),
            )
        }
    )
}
