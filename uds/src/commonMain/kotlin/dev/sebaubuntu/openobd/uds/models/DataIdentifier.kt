/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.uds.models

/**
 * Data identifier.
 *
 * @param dataId Data ID
 * @param expectedBytes Number of bytes expected in the response, null if flexible
 */
@OptIn(ExperimentalUnsignedTypes::class)
class DataIdentifier<T>(
    val dataId: UShort,
    val expectedBytes: Int? = null,
    val responseParser: (UByteArray) -> T = { Unit as T },
) {
    companion object {
        val bootSoftwareIdentification = DataIdentifier<Unit>(
            dataId = 0xF180u,
        )

        val applicationSoftwareIdentification = DataIdentifier<Unit>(
            dataId = 0xF181u,
        )

        val applicationDataIdentification = DataIdentifier<Unit>(
            dataId = 0xF182u,
        )

        val bootSoftwareFingerprint = DataIdentifier<Unit>(
            dataId = 0xF183u,
        )

        val applicationSoftwareFingerprint = DataIdentifier<Unit>(
            dataId = 0xF184u,
        )

        val applicationDataFingerprint = DataIdentifier<Unit>(
            dataId = 0xF185u,
        )

        val activeDiagnosticSession = DataIdentifier<Unit>(
            dataId = 0xF186u,
        )

        val vehicleManufacturerSparePartNumber = DataIdentifier<Unit>(
            dataId = 0xF187u,
        )

        val vehicleManufacturerEcuSoftwareNumber = DataIdentifier<Unit>(
            dataId = 0xF188u,
        )

        val vehicleManufacturerEcuSoftwareVersionNumber = DataIdentifier<Unit>(
            dataId = 0xF189u,
        )

        val systemSupplierIdentifier = DataIdentifier<Unit>(
            dataId = 0xF18Au,
        )

        val ecuManufacturingDate = DataIdentifier<Unit>(
            dataId = 0xF18Bu,
        )

        val ecuSerialNumber = DataIdentifier<Unit>(
            dataId = 0xF18Cu,
        )

        val supportedFunctionalUnits = DataIdentifier<Unit>(
            dataId = 0xF18Du,
        )

        val vehicleManufacturerKitAssemblyPartNumber = DataIdentifier<Unit>(
            dataId = 0xF18Eu,
        )

        val isoSaeReservedStandardized = DataIdentifier<Unit>(
            dataId = 0xF18Fu,
        )

        val vin = DataIdentifier<Unit>(
            dataId = 0xF190u,
        )

        val vehicleManufacturerEcuHardwareNumber = DataIdentifier<Unit>(
            dataId = 0xF191u,
        )

        val systemSupplierECUHardwareNumber = DataIdentifier<Unit>(
            dataId = 0xF192u,
        )

        val systemSupplierEcuHardwareVersionNumber = DataIdentifier<Unit>(
            dataId = 0xF193u,
        )

        val systemSupplierEcuSoftwareNumber = DataIdentifier<Unit>(
            dataId = 0xF194u,
        )

        val systemSupplierEcuSoftwareVersionNumber = DataIdentifier<Unit>(
            dataId = 0xF195u,
        )

        val exhaustRegulationOrTypeApprovalNumber = DataIdentifier<Unit>(
            dataId = 0xF196u,
        )

        val systemNameOrEngineType = DataIdentifier<Unit>(
            dataId = 0xF197u,
        )

        val repairShopCodeOrTesterSerialNumber = DataIdentifier<Unit>(
            dataId = 0xF198u,
        )

        val programmingDate = DataIdentifier<Unit>(
            dataId = 0xF199u,
        )

        val calibrationRepairShopCodeOrCalibrationEquipmentSerialNumber = DataIdentifier<Unit>(
            dataId = 0xF19Au,
        )

        val calibrationDate = DataIdentifier<Unit>(
            dataId = 0xF19Bu,
        )

        val calibrationEquipmentSoftwareNumber = DataIdentifier<Unit>(
            dataId = 0xF19Cu,
        )

        val ecuInstallationDate = DataIdentifier<Unit>(
            dataId = 0xF19Du,
        )

        val odxFile = DataIdentifier<Unit>(
            dataId = 0xF19Eu,
        )

        val entity = DataIdentifier<Unit>(
            dataId = 0xF19Fu,
        )
    }
}
