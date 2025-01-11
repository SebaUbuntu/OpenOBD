/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.app.di

import dev.sebaubuntu.openobd.app.repositories.BluetoothLeRepository
import dev.sebaubuntu.openobd.app.repositories.BluetoothRepository
import dev.sebaubuntu.openobd.app.repositories.ConnectionStatusRepository
import dev.sebaubuntu.openobd.app.repositories.DemoRepository
import dev.sebaubuntu.openobd.app.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.app.repositories.Elm327Repository
import dev.sebaubuntu.openobd.app.repositories.LoggingRepository
import dev.sebaubuntu.openobd.app.repositories.NetworkRepository
import dev.sebaubuntu.openobd.app.repositories.PreferencesRepository
import dev.sebaubuntu.openobd.app.repositories.ProfilesRepository
import dev.sebaubuntu.openobd.app.repositories.UsbRepository
import dev.sebaubuntu.openobd.app.ui.themes.ColorSchemeProvider
import dev.sebaubuntu.openobd.app.utils.PermissionsManager
import dev.sebaubuntu.openobd.app.viewmodels.AppViewModel
import dev.sebaubuntu.openobd.app.viewmodels.BluetoothDevicesViewModel
import dev.sebaubuntu.openobd.app.viewmodels.BluetoothLeDevicesViewModel
import dev.sebaubuntu.openobd.app.viewmodels.ConnectionGatedViewModel
import dev.sebaubuntu.openobd.app.viewmodels.CurrentDataViewModel
import dev.sebaubuntu.openobd.app.viewmodels.CurrentDeviceViewModel
import dev.sebaubuntu.openobd.app.viewmodels.DashboardViewModel
import dev.sebaubuntu.openobd.app.viewmodels.DemoDevicesViewModel
import dev.sebaubuntu.openobd.app.viewmodels.DiagnosticTroubleCodesViewModel
import dev.sebaubuntu.openobd.app.viewmodels.FreezeFrameDataViewModel
import dev.sebaubuntu.openobd.app.viewmodels.LogsViewModel
import dev.sebaubuntu.openobd.app.viewmodels.NetworkDevicesViewModel
import dev.sebaubuntu.openobd.app.viewmodels.SessionInformationViewModel
import dev.sebaubuntu.openobd.app.viewmodels.SettingsViewModel
import dev.sebaubuntu.openobd.app.viewmodels.TerminalViewModel
import dev.sebaubuntu.openobd.app.viewmodels.UsbDevicesViewModel
import dev.sebaubuntu.openobd.app.viewmodels.VehicleInformationViewModel
import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothManager
import dev.sebaubuntu.openobd.backend.bluetoothle.BluetoothLeManager
import dev.sebaubuntu.openobd.backend.demo.DemoManager
import dev.sebaubuntu.openobd.backend.network.NetworkManager
import dev.sebaubuntu.openobd.backend.usb.UsbManager
import dev.sebaubuntu.openobd.elm327.Elm327Manager
import dev.sebaubuntu.openobd.logging.LogBuffer
import dev.sebaubuntu.openobd.logging.LogDevice
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.logging.PrintlnLogDevice
import dev.sebaubuntu.openobd.storage.database.AppDatabase
import dev.sebaubuntu.openobd.storage.preferences.PreferencesManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.time.Clock

val commonModule = module {
    // Coroutines
    single { Dispatchers.IO }
    single { CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>()) }

    // Clock
    single { Clock.System } bind Clock::class

    // Logging
    single { Logger }
    single { PrintlnLogDevice } bind LogDevice::class
    singleOf(::LogBuffer) {
        createdAtStart()
    }

    // Storage
    single(createdAtStart = true) { AppDatabase.get(get()) }
    single(createdAtStart = true) { PreferencesManager.get(get()) }

    // Device managers
    single { BluetoothManager.DEFAULT }
    singleOf(::BluetoothLeManager)
    singleOf(::DemoManager)
    singleOf(::NetworkManager)
    single { UsbManager.DEFAULT }

    // Elm327 manager
    singleOf(::Elm327Manager)

    // Permissions manager
    single { PermissionsManager.DEFAULT }

    // Theming
    single { ColorSchemeProvider.DEFAULT }

    // Repositories
    singleOf(::BluetoothRepository)
    singleOf(::BluetoothLeRepository)
    singleOf(::ConnectionStatusRepository)
    singleOf(::DemoRepository)
    singleOf(::DeviceConnectionRepository)
    singleOf(::Elm327Repository)
    singleOf(::LoggingRepository)
    singleOf(::NetworkRepository)
    singleOf(::PreferencesRepository)
    singleOf(::ProfilesRepository)
    singleOf(::UsbRepository)

    // View models
    viewModelOf(::AppViewModel)
    viewModelOf(::BluetoothDevicesViewModel)
    viewModelOf(::BluetoothLeDevicesViewModel)
    viewModelOf(::ConnectionGatedViewModel)
    viewModelOf(::CurrentDataViewModel)
    viewModelOf(::CurrentDeviceViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::DemoDevicesViewModel)
    viewModelOf(::DiagnosticTroubleCodesViewModel)
    viewModelOf(::FreezeFrameDataViewModel)
    viewModelOf(::LogsViewModel)
    viewModelOf(::NetworkDevicesViewModel)
    viewModelOf(::SessionInformationViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::TerminalViewModel)
    viewModelOf(::UsbDevicesViewModel)
    viewModelOf(::VehicleInformationViewModel)
}
