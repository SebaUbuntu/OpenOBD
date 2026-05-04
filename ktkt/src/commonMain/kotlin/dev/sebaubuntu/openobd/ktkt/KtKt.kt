/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt

import dev.sebaubuntu.openobd.ktkt.interpreter.Evaluator
import dev.sebaubuntu.openobd.ktkt.interpreter.Undefined
import dev.sebaubuntu.openobd.ktkt.lexer.Lexer
import dev.sebaubuntu.openobd.ktkt.parser.Parser

/**
 * Run Kotlin-like scripts from Kotlin.
 */
class KtKt {
    /**
     * Run a script and return its result.
     *
     * @throws RuntimeException If there was an error in the script
     */
    fun run(script: String): Any? {
        try {
            return Lexer(script).tokenize().let { tokens ->
                Parser(tokens).parse().let { nodes ->
                    Evaluator().let { evaluator ->
                        val lastEvaluation = nodes.fold<_, Any?>(Undefined) { _, node ->
                            evaluator.evaluate(node)
                        }

                        when (lastEvaluation) {
                            is Undefined -> Unit
                            else -> lastEvaluation
                        }
                    }
                }
            }
        } catch (e: KtKtException) {
            throw RuntimeException(e.formatMessage(script), e)
        }
    }
}
