/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.di

import dev.sebaubuntu.openobd.backend.bluetooth.BluetoothManager
import dev.sebaubuntu.openobd.backend.demo.DemoManager
import dev.sebaubuntu.openobd.backend.network.NetworkManager
import dev.sebaubuntu.openobd.backend.usb.UsbManager
import dev.sebaubuntu.openobd.logging.LogBuffer
import dev.sebaubuntu.openobd.logging.LogDevice
import dev.sebaubuntu.openobd.logging.Logger
import dev.sebaubuntu.openobd.logging.PrintlnLogDevice
import dev.sebaubuntu.openobd.repositories.BluetoothRepository
import dev.sebaubuntu.openobd.repositories.ConnectionStatusRepository
import dev.sebaubuntu.openobd.repositories.DemoRepository
import dev.sebaubuntu.openobd.repositories.DeviceConnectionRepository
import dev.sebaubuntu.openobd.repositories.LoggingRepository
import dev.sebaubuntu.openobd.repositories.NetworkRepository
import dev.sebaubuntu.openobd.repositories.ObdRepository
import dev.sebaubuntu.openobd.repositories.ProfilesRepository
import dev.sebaubuntu.openobd.repositories.SettingsRepository
import dev.sebaubuntu.openobd.repositories.UsbRepository
import dev.sebaubuntu.openobd.storage.database.AppDatabaseProvider
import dev.sebaubuntu.openobd.utils.PermissionsManager
import dev.sebaubuntu.openobd.viewmodels.BluetoothDevicesViewModel
import dev.sebaubuntu.openobd.viewmodels.ConnectionGatedViewModel
import dev.sebaubuntu.openobd.viewmodels.CurrentDataViewModel
import dev.sebaubuntu.openobd.viewmodels.CurrentDeviceViewModel
import dev.sebaubuntu.openobd.viewmodels.DashboardViewModel
import dev.sebaubuntu.openobd.viewmodels.DemoDevicesViewModel
import dev.sebaubuntu.openobd.viewmodels.DiagnosticTroubleCodesViewModel
import dev.sebaubuntu.openobd.viewmodels.FreezeFrameDataViewModel
import dev.sebaubuntu.openobd.viewmodels.LogsViewModel
import dev.sebaubuntu.openobd.viewmodels.NetworkDevicesViewModel
import dev.sebaubuntu.openobd.viewmodels.SessionInformationViewModel
import dev.sebaubuntu.openobd.viewmodels.SettingsViewModel
import dev.sebaubuntu.openobd.viewmodels.TerminalViewModel
import dev.sebaubuntu.openobd.viewmodels.UsbDevicesViewModel
import dev.sebaubuntu.openobd.viewmodels.VehicleInformationViewModel
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

val commonModule = module {
    // Coroutines
    single { Dispatchers.IO }
    single { CoroutineScope(SupervisorJob() + get<CoroutineDispatcher>()) }

    // Logging
    single { Logger }
    single { PrintlnLogDevice } bind LogDevice::class
    singleOf(::LogBuffer) {
        createdAtStart()
    }

    // Storage
    single(createdAtStart = true) { AppDatabaseProvider.getDatabase(get()) }

    // Device managers
    single { BluetoothManager.DEFAULT }
    singleOf(::DemoManager)
    singleOf(::NetworkManager)
    single { UsbManager.DEFAULT }

    // Permissions manager
    single { PermissionsManager.DEFAULT }

    // Repositories
    singleOf(::BluetoothRepository)
    singleOf(::ConnectionStatusRepository)
    singleOf(::DemoRepository)
    singleOf(::DeviceConnectionRepository)
    singleOf(::LoggingRepository)
    singleOf(::NetworkRepository)
    singleOf(::ObdRepository)
    singleOf(::ProfilesRepository)
    singleOf(::SettingsRepository)
    singleOf(::UsbRepository)

    // View models
    viewModelOf(::BluetoothDevicesViewModel)
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
