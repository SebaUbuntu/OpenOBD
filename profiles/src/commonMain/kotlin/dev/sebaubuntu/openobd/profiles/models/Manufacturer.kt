/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.profiles.models

/**
 * Known car manufacturers.
 *
 * @param displayName The display name
 */
enum class Manufacturer(
    val displayName: String,
) {
    ALFA_ROMEO("Alfa Romeo"),
    AUDI("Audi"),
    BMW("BMW"),
    CITROEN("Citroen"),
    DACIA("Dacia"),
    DS("DS"),
    FERRARI("Ferrari"),
    FIAT("Fiat"),
    FORD("Ford"),
    HONDA("Honda"),
    HYUNDAI("Hyundai"),
    JEEP("Jeep"),
    KIA("Kia"),
    LANCIA("Lancia"),
    LEXUS("Lexus"),
    MASERATI("Maserati"),
    MAZDA("Mazda"),
    MERCEDES_BENZ("Mercedes-Benz"),
    MINI("Mini"),
    NISSAN("Nissan"),
    OPEL("Opel"),
    PEUGEOT("Peugeot"),
    PORSCHE("Porsche"),
    RENAULT("Renault"),
    SEAT("Seat"),
    SKODA("Skoda"),
    SMART("Smart"),
    SUBARU("Subaru"),
    SUZUKI("Suzuki"),
    TOYOTA("Toyota"),
    VOLKSWAGEN("Volkswagen"),
    VOLVO("Volvo"),
    OTHER("Other"),
}
