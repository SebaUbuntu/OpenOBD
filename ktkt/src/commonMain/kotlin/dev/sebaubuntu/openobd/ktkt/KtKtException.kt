/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * KtKt exception.
 */
open class KtKtException(
    val codePosition: Int,
    message: String? = null,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    fun formatMessage(script: String) = buildString {
        val lines = script.take(codePosition).lines()
        val lineNum = lines.size
        val colNum = (lines.lastOrNull()?.length ?: 0) + 1

        append("Error at line $lineNum, column $colNum")
        message?.let { append(": $it") }
        val scriptLines = script.lines()
        if (lineNum <= scriptLines.size) {
            appendLine()
            appendLine(scriptLines[lineNum - 1])
            append(" ".repeat(maxOf(0, colNum - 1)) + "^")
        }
    }.trim()

    @OptIn(ExperimentalContracts::class)
    companion object {
        fun Throwable.toKtKtException(codePosition: Int) = KtKtException(
            codePosition = codePosition,
            message = message,
            cause = this,
        )

        /**
         * If the [block] raises an exception that isn't a [KtKtException], wrap it around a new
         * instance of it and rethrow it.
         */
        fun <T> runtimeRunCatching(codePosition: Int, block: () -> T): T = try {
            block()
        } catch (e: KtKtException) {
            throw e
        } catch (t: Throwable) {
            throw t.toKtKtException(codePosition)
        }

        fun runtimeError(
            message: Any,
            codePosition: Int,
        ): Nothing = throw KtKtException(
            message = message.toString(),
            codePosition = codePosition,
        )

        inline fun runtimeRequire(
            value: Boolean,
            codePosition: Int,
            lazyMessage: () -> Any,
        ) {
            contract {
                returns() implies value
            }

            if (!value) {
                runtimeError(
                    message = lazyMessage(),
                    codePosition = codePosition,
                )
            }
        }

        /**
         * Assert that the [value] is of type [T].
         */
        inline fun <reified T> runtimeRequireIsT(
            value: Any?,
            codePosition: Int,
        ) {
            contract {
                returns() implies (value is T)
            }

            runtimeRequire(value is T, codePosition) {
                "Value is not of type ${T::class.qualifiedName}"
            }
        }

        /**
         * @see Comparable.compareTo
         */
        @Suppress("UNCHECKED_CAST")
        fun Comparable<*>.runtimeCompareTo(
            other: Any?,
            codePosition: Int,
        ): Int {
            runtimeRequireIsT<Comparable<*>>(other, codePosition)

            return compareValues(this as Comparable<Any?>, other as Comparable<Any?>)
        }
    }
}
