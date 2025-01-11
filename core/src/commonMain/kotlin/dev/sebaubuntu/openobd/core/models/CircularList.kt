/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: Apache-2.0
 */

package dev.sebaubuntu.openobd.core.models

/**
 * Circular list.
 */
class CircularList<E> private constructor(
    private val arrayDeque: ArrayDeque<E>,
) : List<E> by arrayDeque {
    constructor(size: Int) : this(ArrayDeque(size))

    fun add(element: E): Boolean {
        while (arrayDeque.size > size) {
            arrayDeque.removeFirstOrNull()
        }

        arrayDeque.addLast(element)

        return true
    }

    fun clear() = arrayDeque.clear()
}
