/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.ext

/**
 * Converts a [UByteArray] to a [UByte]. Assumes big endian.
 */
@ExperimentalUnsignedTypes
inline fun UByteArray.toUByte() = toUNumber(UByte.SIZE_BYTES, 0u, ULong::toUByte, UByte::or)

/**
 * Converts a [UByteArray] to a [UShort]. Assumes big endian.
 */
@ExperimentalUnsignedTypes
inline fun UByteArray.toUShort() = toUNumber(UShort.SIZE_BYTES, 0u, ULong::toUShort, UShort::or)

/**
 * Converts a [UByteArray] to a [UInt]. Assumes big endian.
 */
@ExperimentalUnsignedTypes
inline fun UByteArray.toUInt() = toUNumber(UInt.SIZE_BYTES, 0u, ULong::toUInt, UInt::or)

/**
 * Converts a [UByteArray] to a [ULong]. Assumes big endian.
 */
@ExperimentalUnsignedTypes
inline fun UByteArray.toULong() = toUNumber(ULong.SIZE_BYTES, 0u, ULong::toULong, ULong::or)

@ExperimentalUnsignedTypes
@PublishedApi
internal inline fun <T> UByteArray.toUNumber(
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
