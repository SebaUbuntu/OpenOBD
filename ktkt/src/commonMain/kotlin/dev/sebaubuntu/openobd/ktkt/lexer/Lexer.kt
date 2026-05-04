/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt.lexer

import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeError

/**
 * The lexer.
 */
class Lexer(private val input: String) {
    /**
     * Lexer mode.
     *
     * Equivalent to ANTLR4 mode.
     */
    private enum class LexerMode {
        /**
         * Default mode.
         */
        DEFAULT,

        /**
         * Active inside ( ) and [ ] -> NL is hidden.
         */
        INSIDE,

        /**
         * Active inside " ".
         */
        LINE_STRING,

        /**
         * Active inside """ """.
         */
        MULTILINE_STRING,
    }

    private var position = 0
    private val modeStack = mutableListOf(LexerMode.DEFAULT)
    private val currentMode: LexerMode
        get() = modeStack.last()

    private fun pushMode(mode: LexerMode) {
        modeStack.add(mode)
    }

    private fun popMode() {
        if (modeStack.size > 1) {
            modeStack.removeLast()
        }
    }

    fun tokenize(): List<Token> = buildList {
        while (position < input.length) {

            // Skip hidden channels (WS and comments)
            if (currentMode == LexerMode.DEFAULT || currentMode == LexerMode.INSIDE) {
                skipTrivia()
            }
            if (position >= input.length) {
                break
            }

            // Lex according to current grammar mode
            val token = when (currentMode) {
                LexerMode.DEFAULT -> matchDefaultMode()
                LexerMode.INSIDE -> matchInsideMode()
                LexerMode.LINE_STRING -> matchLineStringMode()
                LexerMode.MULTILINE_STRING -> matchMultiLineStringMode()
            }

            // In INSIDE mode, NL is channeled to HIDDEN (skipped)
            if (currentMode == LexerMode.INSIDE && token.type == Token.Type.NL) {
                continue
            }

            add(token)

            // Mode transitions based on ANTLR actions
            when (token.type) {
                Token.Type.LPAREN, Token.Type.LSQUARE -> pushMode(LexerMode.INSIDE)
                Token.Type.RPAREN, Token.Type.RSQUARE -> popMode()

                // '{' forces default mode, '}' pops whatever expression scope we were in
                Token.Type.LCURL -> pushMode(LexerMode.DEFAULT)
                Token.Type.RCURL -> {
                    if (modeStack.size > 1) popMode()
                }

                Token.Type.QUOTE_OPEN -> pushMode(LexerMode.LINE_STRING)
                Token.Type.TRIPLE_QUOTE_OPEN -> pushMode(LexerMode.MULTILINE_STRING)
                Token.Type.QUOTE_CLOSE -> popMode()
                Token.Type.TRIPLE_QUOTE_CLOSE -> popMode()

                Token.Type.LINE_STR_EXPR_START,
                Token.Type.MULTILINE_STR_EXPR_START -> pushMode(LexerMode.DEFAULT)

                else -> {}
            }
        }
        add(Token(Token.Type.EOF, "", position))
    }

    private fun matchDefaultMode(): Token {
        return matchNextToken(DefaultModeTokens)
    }

    private fun matchInsideMode(): Token {
        return matchNextToken(InsideModeTokens)
    }

    private fun matchLineStringMode(): Token {
        if (input.startsWith("\"", position)) {
            position++

            return Token(
                type = Token.Type.QUOTE_CLOSE,
                value = "\"",
                codePosition = position - 1,
            )
        }

        if (input.startsWith("$", position) && !input.startsWith("\${", position)) {
            return matchSpecificToken(Token.Type.FIELD_IDENTIFIER) ?: Token(
                type = Token.Type.LINE_STR_TEXT,
                value = "$", codePosition = position
            ).also { position++ }
        }

        return matchNextToken(LineStringTokens)
    }

    private fun matchMultiLineStringMode(): Token {
        if (input.startsWith("\"\"\"", position)) {
            position += 3

            return Token(
                type = Token.Type.TRIPLE_QUOTE_CLOSE,
                value = "\"\"\"",
                codePosition = position - 3,
            )
        }

        if (input.startsWith("$", position) && !input.startsWith("\${", position)) {
            return matchSpecificToken(Token.Type.FIELD_IDENTIFIER) ?: Token(
                type = Token.Type.MULTILINE_STR_TEXT,
                value = "$",
                codePosition = position,
            ).also { position++ }
        }

        return matchNextToken(MultiLineStringTokens)
    }

    private fun matchNextToken(allowedTokens: Array<Token.Type>): Token {
        for (type in allowedTokens) {
            type.regex.matchAt(input, position)?.let { match ->
                val value = match.value
                val token = Token(type, value, position)

                position += value.length

                return token
            }
        }

        runtimeError("Unexpected character '${input[position]}'", position)
    }

    private fun matchSpecificToken(type: Token.Type): Token? {
        val match = type.regex.matchAt(input, position) ?: return null
        val token = Token(type, match.value, position)

        position += match.value.length

        return token
    }

    private fun skipTrivia() {
        while (position < input.length) {
            when {
                input.startsWith("/*", position) -> consumeBlockComment()

                input.startsWith("//", position) -> consumeLineComment()

                wsRegex.matchAt(input, position)?.also { match ->
                    position += match.value.length
                } != null -> Unit

                else -> break
            }
        }
    }

    private fun consumeBlockComment() {
        var depth = 1
        position += 2
        while (position < input.length && depth > 0) {
            when {
                input.startsWith("/*", position) -> {
                    depth++
                    position += 2
                }

                input.startsWith("*/", position) -> {
                    depth--
                    position += 2
                }

                else -> position++
            }
        }
    }

    private fun consumeLineComment() {
        while (position < input.length && input[position] != '\n' && input[position] != '\r') {
            position++
        }
    }

    companion object {
        private val wsRegex = """[ \t\u000C]+""".toRegex() // Note: Excludes \n and \r

        /**
         * Grouping tokens to enforce proper matching order and context isolation
         */
        private val DefaultModeTokens = Token.Type.entries.filter {
            when (it) {
                Token.Type.LINE_STR_TEXT,
                Token.Type.LINE_STR_ESCAPED_CHAR,
                Token.Type.LINE_STR_EXPR_START,
                Token.Type.MULTILINE_STR_TEXT,
                Token.Type.MULTILINE_STR_EXPR_START,
                Token.Type.MULTILINE_STRING_QUOTE,
                Token.Type.QUOTE_CLOSE,
                Token.Type.TRIPLE_QUOTE_CLOSE,
                Token.Type.FIELD_IDENTIFIER,
                Token.Type.ERROR_CHARACTER,
                Token.Type.EOF -> false

                else -> true
            }
        }.toTypedArray()

        /**
         * Inside mode is identical to Default, we just hide NL at the evaluation level.
         */
        private val InsideModeTokens = DefaultModeTokens

        private val LineStringTokens = arrayOf(
            Token.Type.LINE_STR_EXPR_START,
            Token.Type.LINE_STR_ESCAPED_CHAR,
            Token.Type.LINE_STR_TEXT,
        )

        private val MultiLineStringTokens = arrayOf(
            Token.Type.MULTILINE_STR_EXPR_START,
            Token.Type.MULTILINE_STRING_QUOTE,
            Token.Type.MULTILINE_STR_TEXT,
        )
    }
}
