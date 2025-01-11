/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.core.ext

/**
 * Converts to a [UByte]. Assumes big endian.
 */
@ExperimentalUnsignedTypes
fun Collection<UByte>.toUByte() = toUNumber(
    UByte.SIZE_BYTES,
    0u,
    ULong::toUByte,
    UByte::or,
)

/**
 * Converts to a [UShort]. Assumes big endian.
 */
@ExperimentalUnsignedTypes
fun Collection<UByte>.toUShort() = toUNumber(
    UShort.SIZE_BYTES,
    0u,
    ULong::toUShort,
    UShort::or,
)

/**
 * Converts to a [UInt]. Assumes big endian.
 */
@ExperimentalUnsignedTypes
fun Collection<UByte>.toUInt() = toUNumber(
    UInt.SIZE_BYTES,
    0u,
    ULong::toUInt,
    UInt::or,
)

/**
 * Converts to a [ULong]. Assumes big endian.
 */
@ExperimentalUnsignedTypes
fun Collection<UByte>.toULong() = toUNumber(
    ULong.SIZE_BYTES,
    0u,
    ULong::toULong,
    ULong::or,
)

@ExperimentalUnsignedTypes
@PublishedApi
internal inline fun <T> Collection<UByte>.toUNumber(
    bytesSize: Int,
    initialValue: T,
    uLongConverter: ULong.() -> T,
    or: T.(T) -> T,
): T {
    require(size == bytesSize) { "Array must contain exactly $bytesSize bytes" }

    var result = initialValue
    forEachIndexed { index, byte ->
        result = result.or(byte.toULong().shl((bytesSize - index - 1) * 8).uLongConverter())
    }
    return result
}
