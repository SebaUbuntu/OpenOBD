/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt.parser

import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeError
import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeRequire
import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeRequireIsT
import dev.sebaubuntu.openobd.ktkt.KtKtException.Companion.runtimeRunCatching
import dev.sebaubuntu.openobd.ktkt.lexer.Token

class Parser(private val tokens: List<Token>) {
    private var current = 0

    /**
     * Parses the entire token stream into a list of statements (the program).
     */
    fun parse(): List<Node.Statement> = buildList {
        consumeSemi()
        while (!isAtEnd) {
            add(parseStatement())
            consumeSemi()
        }
    }

    private fun parseStatement(): Node.Statement = when {
        match(Token.Type.VAL, Token.Type.VAR) -> {
            // If we see `val` or `var`, branch into declaration parsing
            parseVariableDeclaration(isMutable = previous().type == Token.Type.VAR)
        }

        match(Token.Type.WHILE) -> {
            val startPos = previous().codePosition

            skipNewlines()

            consume(Token.Type.LPAREN) { "Expect '(' after 'while'." }
            val condition = parseExpression(Precedence.NONE)
            consume(Token.Type.RPAREN) { "Expect ')' after condition." }

            skipNewlines()

            val body = when {
                match(Token.Type.SEMICOLON) -> null
                else -> parseExpression(Precedence.NONE)
            }

            Node.Statement.While(
                condition = condition,
                body = body,
                codePosition = startPos,
            )
        }

        match(Token.Type.DO) -> {
            val startPos = previous().codePosition

            skipNewlines()

            val body = parseExpression(Precedence.NONE)

            skipNewlines()

            consume(Token.Type.WHILE) { "Expected 'while' after `do` block." }

            skipNewlines()

            consume(Token.Type.LPAREN) { "Expect '(' after 'while'." }
            val condition = parseExpression(Precedence.NONE)
            consume(Token.Type.RPAREN) { "Expect ')' after condition." }

            Node.Statement.DoWhile(
                body = body,
                condition = condition,
                codePosition = startPos,
            )
        }

        peek().type in listOf(
            Token.Type.FUN,
            Token.Type.PUBLIC,
            Token.Type.PRIVATE,
            Token.Type.PROTECTED,
            Token.Type.INTERNAL,
        ) -> parseFunctionDeclaration()

        else -> {
            // Otherwise, it must be an expression statement (e.g., a function call or math)
            parseExpressionStatement()
        }
    }

    private fun parseVariableDeclaration(isMutable: Boolean): Node.Statement.VariableDeclaration {
        skipNewlines()

        val startPos = peek().codePosition

        val nameToken = parseSimpleIdentifier()

        val typeDeclaration = when (match(Token.Type.COLON)) {
            true -> parseType()
            false -> null
        }

        val initializer = when (match(Token.Type.ASSIGNMENT)) {
            true -> {
                skipNewlines()
                parseExpression(Precedence.NONE)
            }

            false -> null
        }

        return Node.Statement.VariableDeclaration(
            name = nameToken,
            isMutable = isMutable,
            typeDeclaration = typeDeclaration,
            initializer = initializer,
            codePosition = startPos,
        )
    }

    private fun parseFunctionDeclaration(): Node.Statement.FunctionDeclaration {
        val startPos = peek().codePosition

        skipNewlines()

        // Function modifiers
        var visibility: Node.Statement.FunctionDeclaration.Visibility? = null
        while (
            match(
                Token.Type.PUBLIC,
                Token.Type.PRIVATE,
                Token.Type.PROTECTED,
                Token.Type.INTERNAL,
            )
        ) {
            when (previous().type) {
                Token.Type.PUBLIC -> {
                    require(visibility == null) { "Incompatible function modifiers" }
                    visibility = Node.Statement.FunctionDeclaration.Visibility.PUBLIC
                }

                Token.Type.PRIVATE -> {
                    require(visibility == null) { "Incompatible function modifiers" }
                    visibility = Node.Statement.FunctionDeclaration.Visibility.PRIVATE
                }

                Token.Type.PROTECTED -> {
                    require(visibility == null) { "Incompatible function modifiers" }
                    visibility = Node.Statement.FunctionDeclaration.Visibility.PROTECTED
                }

                Token.Type.INTERNAL -> {
                    require(visibility == null) { "Incompatible function modifiers" }
                    visibility = Node.Statement.FunctionDeclaration.Visibility.INTERNAL
                }

                else -> error("Should never happen")
            }

            skipNewlines()
        }

        consume(Token.Type.FUN) { "Expected 'fun'" }

        val typeParameters = when (peekNextAfterNewlines().type) {
            Token.Type.LANGLE -> {
                skipNewlines()
                parseTypeParameters()
            }

            else -> listOf()
        }

        // TODO: Receiver type

        skipNewlines()

        val name = parseSimpleIdentifier()

        skipNewlines()

        // Arguments
        val parameters = parseFunctionValueParameters()

        // Return type
        val returnType = when {
            match(Token.Type.COLON) -> parseType()

            else -> when (peek().type) {
                Token.Type.LCURL -> Node.TypeDeclaration.Literal(
                    "kotlin.Unit",
                    typeParameters = listOf(),
                    isNullable = false,
                    codePosition = startPos,
                )

                Token.Type.ASSIGNMENT -> null

                else -> runtimeError(
                    message = "The type cannot be inferred if no body is defined.",
                    codePosition = peek().codePosition,
                )
            }
        }

        skipNewlines()

        // Function body
        val body = when {
            match(Token.Type.ASSIGNMENT) -> parseExpression(Precedence.ASSIGNMENT)
            check(Token.Type.LCURL) -> parseBlock()
            else -> null
        }

        return Node.Statement.FunctionDeclaration(
            name = name,
            visibility = visibility ?: Node.Statement.FunctionDeclaration.Visibility.PUBLIC,
            typeParameters = typeParameters,
            receiverType = null, // TODO
            parameters = parameters,
            returnType = returnType,
            body = body,
            codePosition = startPos,
        )
    }

    private fun parseTypeParameters() = buildList {
        consume(Token.Type.LANGLE) { "Expecting opening of type parameters" }

        skipNewlines()

        do {
            if (check(Token.Type.RANGLE)) {
                break
            }

            add(parseTypeParameter())

            skipNewlines()
        } while (match(Token.Type.COMMA))

        consume(Token.Type.RANGLE) { "Expecting closure of type parameters" }
    }

    private fun parseTypeParameter(): Pair<String, Node.TypeDeclaration?> {
        skipNewlines()

        val name = consume(Token.Type.IDENTIFIER) { "Expected type parameter identifier." }

        val type = when (peekNextAfterNewlines().type) {
            Token.Type.COLON -> {
                skipNewlines()
                parseType()
            }

            else -> null
        }

        return name.value to type
    }

    /**
     * [Reference](
     * https://kotlinlang.org/spec/syntax-and-grammar.html#grammar-rule-functionValueParameters
     * )
     */
    private fun parseFunctionValueParameters() = buildList {
        consume(Token.Type.LPAREN) { "Expected start of parameters" }

        skipNewlines()

        do {
            if (peekNextAfterNewlines().type == Token.Type.RPAREN) {
                break
            }

            add(parseFunctionValueParameter())

            skipNewlines()
        } while (match(Token.Type.COMMA))

        consume(Token.Type.RPAREN) { "Expected start of parameters" }
    }

    private fun parseFunctionValueParameter(): Node.Statement.FunctionDeclaration.ValueParameter {
        val parameter = parseParameter()

        val defaultValue = when (peekNextAfterNewlines().type) {
            Token.Type.ASSIGNMENT -> {
                skipNewlines()
                advance()
                skipNewlines()
                parseExpression(Precedence.NONE)
            }

            else -> null
        }

        return Node.Statement.FunctionDeclaration.ValueParameter(
            name = parameter.first,
            type = parameter.second,
            defaultValue = defaultValue,
        )
    }

    /**
     * Parse a parameter with a required type.
     */
    private fun parseParameter(): Pair<String, Node.TypeDeclaration> {
        val name = parseSimpleIdentifier()

        skipNewlines()

        consume(Token.Type.COLON) { "Expected argument type after the name" }

        val type = parseType()

        return name to type
    }

    private fun parseExpressionStatement(): Node.Statement.Expression {
        val startPos = peek().codePosition
        val expr = parseExpression(Precedence.NONE)

        return Node.Statement.Expression(expr, startPos)
    }

    /**
     * Pratt parser precedence.
     */
    private fun getBindingPower(type: Token.Type): Precedence {
        return when (type) {
            Token.Type.MULT,
            Token.Type.DIV,
            Token.Type.MOD -> Precedence.FACTOR

            Token.Type.ADD,
            Token.Type.SUB -> Precedence.TERM

            Token.Type.LANGLE,
            Token.Type.RANGLE,
            Token.Type.LE,
            Token.Type.GE -> Precedence.COMPARISON

            Token.Type.EQEQ,
            Token.Type.EXCL_EQ,
            Token.Type.EQEQEQ,
            Token.Type.EXCL_EQEQ -> Precedence.EQUALITY

            Token.Type.CONJ -> Precedence.AND

            Token.Type.DISJ -> Precedence.OR

            Token.Type.ASSIGNMENT -> Precedence.ASSIGNMENT

            Token.Type.LPAREN,
            Token.Type.DOT -> Precedence.CALL

            Token.Type.INCR,
            Token.Type.DECR -> Precedence.POSTFIX

            else -> Precedence.NONE
        }
    }

    private fun parseExpression(precedence: Precedence = Precedence.NONE): Node.Expression {
        var left = parsePrefix()

        while (precedence.ordinal < getBindingPower(peek().type).ordinal) {
            val operatorToken = advance()
            left = parseInfix(left, operatorToken)
        }

        return left
    }

    private fun parsePrefix(): Node.Expression = peek().let { token ->
        when (token.type) {
            // Non-numeric literals
            Token.Type.NULL_LIT -> {
                advance()
                Node.Expression.Literal.Null(
                    codePosition = token.codePosition,
                )
            }

            Token.Type.BOOLEAN_LIT -> {
                advance()
                Node.Expression.Literal.Boolean(
                    value = token.value.toBooleanStrict(),
                    codePosition = token.codePosition
                )
            }

            Token.Type.CHARACTER_LIT -> {
                advance()
                Node.Expression.Literal.Char(
                    token.value.first(), // TODO
                    token.codePosition
                )
            }

            // Numeric literals
            Token.Type.UNSIGNED_LIT,
            Token.Type.LONG_LIT,
            Token.Type.HEX_LIT,
            Token.Type.BIN_LIT,
            Token.Type.INTEGER_LIT -> parseIntegerLiteral()

            Token.Type.REAL_LIT -> parseRealLiteral()

            Token.Type.QUOTE_OPEN,
            Token.Type.TRIPLE_QUOTE_OPEN -> parseStringLiteral()

            // Unary prefix (e.g., -5, !true)
            Token.Type.SUB,
            Token.Type.ADD,
            Token.Type.EXCL_NO_WS,
            Token.Type.EXCL_WS -> parseUnaryPrefix()

            // Grouping ( e.g., (1 + 2) )
            Token.Type.LPAREN -> {
                advance()
                val expr = parseExpression(Precedence.NONE)
                consume(Token.Type.RPAREN) { "Expect ')' after expression." }
                expr
            }

            Token.Type.LCURL -> parseBlock()

            Token.Type.IF -> {
                val startPos = advance().codePosition

                consume(Token.Type.LPAREN) { "Expect '(' after 'if'." }

                val condition = parseExpression(Precedence.NONE)

                consume(Token.Type.RPAREN) { "Expect ')' after if condition." }

                val thenBranch = parseExpression(Precedence.NONE)

                val elseBranch = when (match(Token.Type.ELSE)) {
                    true -> parseExpression(Precedence.NONE)
                    false -> null
                }

                Node.Expression.If(
                    condition = condition,
                    thenBranch = thenBranch,
                    elseBranch = elseBranch,
                    codePosition = startPos
                )
            }

            Token.Type.RETURN -> {
                advance()

                Node.Expression.Return(
                    expression = parseExpression(Precedence.NONE),
                    codePosition = token.codePosition,
                )
            }

            Token.Type.INCR,
            Token.Type.DECR -> {
                val prefix = advance()

                val target = parseExpression(Precedence.UNARY)

                runtimeRequireIsT<Node.Expression.MemoryReference>(target, prefix.codePosition)

                val type = when (prefix.type) {
                    Token.Type.INCR -> Node.Expression.Unary.Assignment.Type.INCREMENT
                    Token.Type.DECR -> Node.Expression.Unary.Assignment.Type.DECREMENT
                    else -> runtimeError("Unexpected prefix token", prefix.codePosition)
                }

                Node.Expression.Unary.Assignment(
                    argument = target,
                    type = type,
                    prefix = true,
                    prefix.codePosition,
                )
            }

            else -> when (token.type.isSimpleIdentifier()) {
                true -> {
                    advance()
                    Node.Expression.MemoryReference.Variable(
                        name = token.value,
                        codePosition = token.codePosition,
                    )
                }

                false -> runtimeError(
                    message = "Expected expression, found ${token.type}",
                    codePosition = token.codePosition,
                )
            }
        }
    }

    /**
     * Parse an integer literal.
     */
    private fun parseIntegerLiteral(): Node.Expression.Literal<*> = advance().let { token ->
        // Token validation
        when (token.type) {
            Token.Type.UNSIGNED_LIT,
            Token.Type.LONG_LIT,
            Token.Type.HEX_LIT,
            Token.Type.BIN_LIT,
            Token.Type.INTEGER_LIT -> Unit

            else -> runtimeError(
                message = "Unexpected token type for integer literal",
                codePosition = token.codePosition
            )
        }

        runtimeRequire(!token.value.endsWith('l'), token.codePosition) {
            "Use 'L' instead of 'l'."
        }

        val isUnsigned = token.type == Token.Type.UNSIGNED_LIT
        val isLong = when {
            token.type == Token.Type.LONG_LIT -> true
            token.value.endsWith('L') -> true
            else -> false
        }

        // Strip formatting underscores and remove suffixes
        val raw = token.value
            .replace("_", "")
            .trimEnd('L', 'u', 'U')

        val (radix, valueStr) = when {
            raw.startsWith("0x", ignoreCase = true) -> 16 to raw.substring(2)
            raw.startsWith("0b", ignoreCase = true) -> 2 to raw.substring(2)
            else -> 10 to raw
        }

        runtimeRunCatching(token.codePosition) {
            when {
                isUnsigned && isLong -> Node.Expression.Literal.ULong(
                    value = valueStr.toULong(radix),
                    codePosition = token.codePosition
                )

                isUnsigned -> Node.Expression.Literal.UInt(
                    value = valueStr.toUInt(radix),
                    codePosition = token.codePosition
                )

                isLong -> Node.Expression.Literal.Long(
                    // Use ULong parse to prevent crash on large Hex/Bin, then cast
                    value = when (radix) {
                        10 -> valueStr.toLong(radix)
                        else -> valueStr.toULong(radix).toLong()
                    },
                    codePosition = token.codePosition
                )

                else -> Node.Expression.Literal.Int(
                    // Use UInt parse to prevent crash on large hex/bin (e.g. 0xFFFFFFFF), then cast
                    value = when (radix) {
                        10 -> valueStr.toInt(radix)
                        else -> valueStr.toUInt(radix).toInt()
                    },
                    codePosition = token.codePosition
                )
            }
        }
    }

    /**
     * Parses floating-point and double-precision numbers.
     */
    private fun parseRealLiteral(): Node.Expression.Literal<*> = advance().let { token ->
        runtimeRequire(token.type == Token.Type.REAL_LIT, token.codePosition) {
            "Unexpected token type for real literal"
        }

        val isFloat = token.value.endsWith('f', ignoreCase = true)

        val raw = token.value
            .replace("_", "")
            .trimEnd('f', 'F')

        runtimeRunCatching(token.codePosition) {
            when (isFloat) {
                true -> Node.Expression.Literal.Float(
                    value = raw.toFloat(),
                    codePosition = token.codePosition,
                )

                false -> Node.Expression.Literal.Double(
                    value = raw.toDouble(),
                    codePosition = token.codePosition,
                )
            }
        }
    }

    private fun parseStringLiteral(): Node.Expression.Literal.String {
        val startToken = advance()

        val closeTokenType = when (startToken.type) {
            Token.Type.TRIPLE_QUOTE_OPEN -> Token.Type.TRIPLE_QUOTE_CLOSE
            Token.Type.QUOTE_OPEN -> Token.Type.QUOTE_CLOSE
            else -> runtimeError(
                message = "Unexpected opening token for string literal",
                codePosition = startToken.codePosition,
            )
        }

        val stringContent = StringBuilder()
        while (!check(closeTokenType) && !isAtEnd) {
            when {
                match(
                    Token.Type.LINE_STR_TEXT,
                    Token.Type.LINE_STR_ESCAPED_CHAR,
                    Token.Type.MULTILINE_STR_TEXT,
                    Token.Type.MULTILINE_STRING_QUOTE
                ) -> stringContent.append(previous().value)

                match(
                    Token.Type.LINE_STR_EXPR_START,
                    Token.Type.MULTILINE_STR_EXPR_START,
                ) -> {
                    // TODO: String interpolation
                    runtimeError("String interpolation is not yet supported.", peek().codePosition)
                }

                else -> runtimeError(
                    message = "Unexpected token in string literal",
                    codePosition = peek().codePosition,
                )
            }
        }

        consume(closeTokenType) { "Expect closing quote to close the string." }

        return Node.Expression.Literal.String(
            value = stringContent.toString(),
            codePosition = startToken.codePosition,
        )
    }

    private fun parseUnaryPrefix(): Node.Expression.Unary.Value {
        val prefix = advance()

        skipNewlines()

        val argument = parseExpression(Precedence.UNARY)

        val type = when (prefix.type) {
            Token.Type.SUB -> Node.Expression.Unary.Value.Type.MINUS
            Token.Type.ADD -> Node.Expression.Unary.Value.Type.PLUS
            Token.Type.EXCL_NO_WS, Token.Type.EXCL_WS -> Node.Expression.Unary.Value.Type.NOT
            else -> runtimeError(
                message = "Unary prefix expected.",
                codePosition = prefix.codePosition,
            )
        }

        return Node.Expression.Unary.Value(
            argument = argument,
            type = type,
            codePosition = prefix.codePosition
        )
    }

    private fun parseInfix(
        left: Node.Expression,
        operatorToken: Token,
    ): Node.Expression = when (operatorToken.type) {
        Token.Type.LPAREN -> {
            val arguments = buildList {
                do {
                    if (check(Token.Type.RPAREN)) {
                        break
                    }

                    val parameterName = when (peekNext().type) {
                        Token.Type.COLON -> {
                            advance()
                            consume(Token.Type.IDENTIFIER) {
                                "Expected identifier for parameter name"
                            }.value
                        }

                        else -> null
                    }

                    add(parameterName to parseExpression(Precedence.NONE))
                } while (match(Token.Type.COMMA))

                consume(Token.Type.RPAREN) { "Expect ')' after arguments." }
            }

            Node.Expression.Invocation(
                callable = left,
                arguments = arguments,
                codePosition = left.codePosition,
            )
        }

        Token.Type.DOT -> {
            skipNewlines()

            // The left parameter is our receiver (e.g., `value`)
            val nameToken = consume(Token.Type.IDENTIFIER) {
                "Expect property name after '.'."
            }

            Node.Expression.MemoryReference.Property(
                receiver = left,
                propertyName = nameToken.value,
                codePosition = left.codePosition,
            )
        }

        Token.Type.INCR,
        Token.Type.DECR -> {
            // Ensure the left side is an L-Value
            runtimeRequireIsT<Node.Expression.MemoryReference>(left, operatorToken.codePosition)

            val type = when (operatorToken.type) {
                Token.Type.INCR -> Node.Expression.Unary.Assignment.Type.INCREMENT
                Token.Type.DECR -> Node.Expression.Unary.Assignment.Type.DECREMENT
            }

            Node.Expression.Unary.Assignment(
                argument = left,
                type = type,
                prefix = false,
                codePosition = left.codePosition
            )
        }

        else -> {
            val bindingPower = getBindingPower(operatorToken.type)

            // If it's right-associative (like assignment), subtract 1 from the power
            val rightPower = when (operatorToken.type) {
                Token.Type.ASSIGNMENT -> Precedence.entries[bindingPower.ordinal - 1]
                else -> bindingPower
            }

            skipNewlines()
            val right = parseExpression(rightPower)

            when (operatorToken.type) {
                Token.Type.ASSIGNMENT -> Node.Expression.Binary.Assignment(
                    left = when (left) {
                        is Node.Expression.MemoryReference -> left
                        else -> runtimeError(
                            message = "Invalid assignment target. Expected a variable or property.",
                            codePosition = left.codePosition,
                        )
                    },
                    right = right,
                    codePosition = left.codePosition,
                )

                else -> when (operatorToken.type) {
                    // Math
                    Token.Type.ADD -> Node.Expression.Binary.Value.Type.ADD
                    Token.Type.SUB -> Node.Expression.Binary.Value.Type.SUBTRACT
                    Token.Type.MULT -> Node.Expression.Binary.Value.Type.MULTIPLY
                    Token.Type.DIV -> Node.Expression.Binary.Value.Type.DIVIDE
                    Token.Type.MOD -> Node.Expression.Binary.Value.Type.REMAINDER

                    // Comparison
                    Token.Type.LANGLE -> Node.Expression.Binary.Value.Type.LESS_THAN
                    Token.Type.RANGLE -> Node.Expression.Binary.Value.Type.GREATER_THAN
                    Token.Type.LE -> Node.Expression.Binary.Value.Type.LESS_THAN_OR_EQUAL
                    Token.Type.GE -> Node.Expression.Binary.Value.Type.GREATER_THAN_OR_EQUAL

                    // Equality
                    Token.Type.EQEQ -> Node.Expression.Binary.Value.Type.EQUAL
                    Token.Type.EXCL_EQ -> Node.Expression.Binary.Value.Type.NOT_EQUAL
                    Token.Type.EQEQEQ -> Node.Expression.Binary.Value.Type.IDENTITY_EQUAL
                    Token.Type.EXCL_EQEQ -> Node.Expression.Binary.Value.Type.NOT_IDENTITY_EQUAL

                    // Logical
                    Token.Type.CONJ -> Node.Expression.Binary.Value.Type.AND
                    Token.Type.DISJ -> Node.Expression.Binary.Value.Type.OR

                    else -> runtimeError(
                        "Unknown binary operator ${operatorToken.type}",
                        codePosition = left.codePosition,
                    )
                }.let { type ->
                    Node.Expression.Binary.Value(
                        left = left,
                        right = right,
                        type = type,
                        codePosition = left.codePosition,
                    )
                }
            }
        }
    }

    private fun parseType(): Node.TypeDeclaration {
        val startPos = peek().codePosition

        // CASE 1: Standard lambda (no receiver) -> e.g., (Int, String) -> Boolean
        if (match(Token.Type.LPAREN)) {
            return parseLambdaTypeTail(receiver = null, startPos)
        }

        // CASE 2: Literal type (possibly with generics)
        val identifier = consume(Token.Type.IDENTIFIER) { "Expect type name." }

        val typeParameters = when (peekNextAfterNewlines().type) {
            Token.Type.LANGLE -> {
                skipNewlines()
                parseTypeParameters()
            }

            else -> listOf()
        }

        var literalType = identifier.value

        if (match(Token.Type.DOT)) {
            if (check(Token.Type.LPAREN)) {
                // Extension lambda -> e.g., String.(Int) -> Boolean
                consume(Token.Type.LPAREN) {
                    "Expect '(' for lambda parameters after receiver type."
                }

                // Pass our parsed literalType as the receiver!
                return parseLambdaTypeTail(
                    receiver = Node.TypeDeclaration.Literal(
                        name = identifier.value,
                        typeParameters = typeParameters,
                        codePosition = startPos,
                        isNullable = false,
                    ),
                    startPos
                )
            } else {
                // Continuation of the literal type
                literalType += "."
                literalType += consume(Token.Type.IDENTIFIER) {
                    "Expected identifier."
                }.value
            }
        }

        val isNullable = match(Token.Type.QUEST_WS)

        // If it wasn't an extension lambda, just return the literal
        return Node.TypeDeclaration.Literal(
            name = literalType,
            typeParameters = typeParameters,
            codePosition = startPos,
            isNullable = isNullable,
        )
    }

    /**
     * Helper to parse the `(args) -> ReturnType` part of a lambda.
     */
    private fun parseLambdaTypeTail(
        receiver: Node.TypeDeclaration?,
        startPos: Int
    ): Node.TypeDeclaration.Lambda {
        val arguments = mutableListOf<Pair<String?, Node.TypeDeclaration>>()

        // Parse arguments if the parenthesis isn't immediately closed
        if (!check(Token.Type.RPAREN)) {
            do {
                var argName: String? = null

                // If we see an Identifier followed by a Colon, it's a named argument (e.g., x: Int)
                if (peek().type == Token.Type.IDENTIFIER && peekNext().type == Token.Type.COLON) {
                    argName = consume(Token.Type.IDENTIFIER) {
                        "Expect parameter name."
                    }.value

                    consume(Token.Type.COLON) { "Expect ':' after parameter name." }
                }

                val argType = parseType()

                arguments.add(Pair(argName, argType))
            } while (match(Token.Type.COMMA))
        }

        consume(Token.Type.RPAREN) { "Expect ')' after lambda parameters." }
        consume(Token.Type.ARROW) { "Expect '->' after lambda parameters." }

        val returnType = parseType()

        return Node.TypeDeclaration.Lambda(
            receiverType = receiver,
            arguments = arguments,
            returnType = returnType,
            codePosition = startPos,
            isNullable = false, // TODO!
        )
    }

    private fun parseBlock(): Node.Expression.Block {
        val startPos = consume(Token.Type.LCURL) { "Expected a block." }.codePosition

        val statements = buildList {
            consumeSemi()
            while (!check(Token.Type.RCURL) && !isAtEnd) {
                add(parseStatement())
                consumeSemi()
            }
        }

        consume(Token.Type.RCURL) {
            "Expect '}' after a block."
        }

        return Node.Expression.Block(statements, startPos)
    }

    /**
     * [Reference](
     * https://kotlinlang.org/spec/syntax-and-grammar.html#grammar-rule-simpleIdentifier
     * )
     */
    private fun Token.Type.isSimpleIdentifier() = when (this) {
        Token.Type.IDENTIFIER,
        Token.Type.ABSTRACT,
        Token.Type.ANNOTATION,
        Token.Type.BY,
        Token.Type.CATCH,
        Token.Type.COMPANION,
        Token.Type.CONSTRUCTOR,
        Token.Type.CROSSINLINE,
        Token.Type.DATA,
        Token.Type.DYNAMIC,
        Token.Type.ENUM,
        Token.Type.EXTERNAL,
        Token.Type.FINAL,
        Token.Type.FINALLY,
        Token.Type.GET,
        Token.Type.IMPORT,
        Token.Type.INFIX,
        Token.Type.INIT,
        Token.Type.INLINE,
        Token.Type.INNER,
        Token.Type.INTERNAL,
        Token.Type.LATEINIT,
        Token.Type.NOINLINE,
        Token.Type.OPEN,
        Token.Type.OPERATOR,
        Token.Type.OUT,
        Token.Type.OVERRIDE,
        Token.Type.PRIVATE,
        Token.Type.PROTECTED,
        Token.Type.PUBLIC,
        Token.Type.REIFIED,
        Token.Type.SEALED,
        Token.Type.TAILREC,
        Token.Type.SET,
        Token.Type.VARARG,
        Token.Type.WHERE,
        Token.Type.FIELD,
        Token.Type.PROPERTY,
        Token.Type.RECEIVER,
        Token.Type.PARAM,
        Token.Type.SETPARAM,
        Token.Type.DELEGATE,
        Token.Type.FILE,
        Token.Type.EXPECT,
        Token.Type.ACTUAL,
        Token.Type.CONST,
        Token.Type.SUSPEND,
        Token.Type.VALUE -> true

        else -> false
    }

    private fun parseSimpleIdentifier(): String {
        return when (peek().type.isSimpleIdentifier()) {
            true -> advance().value
            false -> runtimeError("Expected a simple identifier", peek().codePosition)
        }
    }

    /**
     * Matches the `semi` and `semis` rules from the Kotlin grammar.
     * Consumes one or more semicolons or newlines.
     */
    private fun consumeSemi(): Boolean {
        var consumed = false
        while (match(Token.Type.SEMICOLON, Token.Type.NL)) {
            consumed = true
        }
        return consumed
    }

    /**
     * Skips newlines. Used after operators that unambiguously expect a right-hand side.
     */
    private fun skipNewlines() {
        while (match(Token.Type.NL)) {
            // Just consume them
        }
    }

    // Helpers

    /**
     * Whether the current token is the end one
     */
    private val isAtEnd: Boolean
        get() = peek().type == Token.Type.EOF

    /**
     * Get the current token.
     */
    private fun peek(): Token = tokens[current]

    /**
     * Return the previous token.
     */
    private fun previous(): Token = tokens[current - 1]

    /**
     * Return the current token and move to the next one.
     */
    private fun advance(): Token {
        if (!isAtEnd) {
            current++
        }
        return previous()
    }

    /**
     * Check if the next token is of the provided [Token.Type].
     */
    private fun check(type: Token.Type): Boolean {
        if (isAtEnd) {
            return false
        }

        return peek().type == type
    }

    /**
     * Check if the next token matches at least one of the provided [Token.Type].
     *
     * If it does, consume it and return.
     */
    private fun match(vararg types: Token.Type): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }

        return false
    }

    private fun consume(
        type: Token.Type,
        lazyErrorMessage: () -> String,
    ): Token {
        if (check(type)) {
            return advance()
        }

        runtimeError(
            message = "${lazyErrorMessage()} at position ${peek().codePosition}",
            codePosition = peek().codePosition,
        )
    }

    /**
     * Looks at the token immediately after the current one
     */
    private fun peekNext(): Token {
        if (current + 1 >= tokens.size) {
            return tokens.last()
        }

        return tokens[current + 1]
    }

    /**
     * Look at the token after skipping the newline tokens.
     */
    private fun peekNextAfterNewlines(): Token {
        var peekIndex = current
        while (peekIndex < tokens.size && tokens[peekIndex].type == Token.Type.NL) {
            peekIndex++
        }

        return tokens[peekIndex]
    }
}
