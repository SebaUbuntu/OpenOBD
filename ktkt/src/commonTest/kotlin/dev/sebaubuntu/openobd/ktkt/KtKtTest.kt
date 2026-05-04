/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt

import dev.sebaubuntu.openobd.ktkt.interpreter.Evaluator
import dev.sebaubuntu.openobd.ktkt.interpreter.Undefined
import dev.sebaubuntu.openobd.ktkt.lexer.Lexer
import dev.sebaubuntu.openobd.ktkt.parser.Parser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class KtKtTest {
    @Test
    fun testGenericFunction() = """
        fun <T> identity(value: T): T {
            return value
        }
        
        identity("Hello generics")
    """.trimIndent().evaluateScript().let {
        assertEquals("Hello generics", it)
    }

    @Test
    fun testComplicatedScript() = """
        /**
         * https://xkcd.com/221/
         */
        fun getRandomNumber(): Int
        {
            return 4 // chosen by fair dice roll.
                     // guaranteed to be random.
        }
        
        "Hi from KtKt! Random value: " + getRandomNumber()
    """.trimIndent().evaluateScript().let {
        assertIs<String>(it)
        assertEquals("Hi from KtKt! Random value: 4", it)
    }

    companion object {
        private inline fun <T> String.withExceptionWrapping(block: () -> T) = try {
            block()
        } catch (e: KtKtException) {
            throw RuntimeException(e.formatMessage(this), e)
        }

        fun String.tokenizeScript() = withExceptionWrapping {
            Lexer(this).tokenize().also { tokens ->
                println("Tokens:")
                tokens.forEach { token ->
                    println("- $token")
                }
                println()
            }
        }

        fun String.parseScript() = withExceptionWrapping {
            tokenizeScript().let { tokens ->
                Parser(tokens).parse().also { nodes ->
                    println("Nodes")
                    nodes.forEach { node ->
                        println("- $node")
                    }
                    println()
                }
            }
        }

        fun String.evaluateScript() = withExceptionWrapping {
            parseScript().let { nodes ->
                val evaluator = Evaluator()

                var lastEvaluation: Any? = Undefined

                println("Evaluation:")
                nodes.forEach { node ->
                    evaluator.evaluate(node).let { evaluation ->
                        println("- $evaluation")
                        lastEvaluation = evaluation
                    }
                }

                when (lastEvaluation) {
                    is Undefined -> Unit
                    else -> lastEvaluation
                }
            }
        }
    }
}
