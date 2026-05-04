/*
 * SPDX-FileCopyrightText: Sebastiano Barezzi
 * SPDX-License-Identifier: GPL-3.0-or-later
 */

package dev.sebaubuntu.openobd.ktkt.lexer

/**
 * Tokens.
 *
 * Derived from [the official ANTLR4 rules](
 * https://github.com/Kotlin/kotlin-spec/tree/release/grammar/src/main/antlr
 * ).
 */
data class Token(
    val type: Type,
    val value: String,
    val codePosition: Int,
) {
    enum class Type(val regex: Regex) {
        // Separators and operations
        RESERVED("""\.\.\.""".toRegex()),
        DOUBLE_SEMICOLON(""";;""".toRegex()),
        COLONCOLON("""::""".toRegex()),
        RANGE_UNTIL("""\.\.<""".toRegex()),
        RANGE("""\.\.""".toRegex()),
        DOUBLE_ARROW("""=>""".toRegex()),
        ARROW("""->""".toRegex()),
        EXCL_EQEQ("""!==""".toRegex()),
        EXCL_EQ("""!=""".toRegex()),
        EQEQEQ("""===""".toRegex()),
        EQEQ("""==""".toRegex()),
        ADD_ASSIGNMENT("""\+=""".toRegex()),
        SUB_ASSIGNMENT("""-=""".toRegex()),
        MULT_ASSIGNMENT("""\*=""".toRegex()),
        DIV_ASSIGNMENT("""/=""".toRegex()),
        MOD_ASSIGNMENT("""%=""".toRegex()),
        INCR("""\+\+""".toRegex()),
        DECR("""--""".toRegex()),
        CONJ("""&&""".toRegex()),
        DISJ("""\|\|""".toRegex()),
        LE("""<=""".toRegex()),
        GE(""">=""".toRegex()),
        AS_SAFE("""as\?""".toRegex()),
        NOT_IS("""!is\b""".toRegex()),
        NOT_IN("""!in\b""".toRegex()),
        AT_BOTH_WS("""(?<=\s|^)@(?=\s|$)""".toRegex()),
        AT_PRE_WS("""(?<=\s|^)@""".toRegex()),
        AT_POST_WS("""@(?=\s|$)""".toRegex()),
        AT_NO_WS("""@""".toRegex()),
        QUEST_WS("""\?(?=\s|$)""".toRegex()),
        QUEST_NO_WS("""\?""".toRegex()),
        DOT("""\.""".toRegex()),
        COMMA(""",""".toRegex()),
        LPAREN("""\(""".toRegex()),
        RPAREN("""\)""".toRegex()),
        LSQUARE("""\[""".toRegex()),
        RSQUARE("""]""".toRegex()),
        LCURL("""\{""".toRegex()),
        RCURL("""\}""".toRegex()),
        MULT("""\*""".toRegex()),
        MOD("""%""".toRegex()),
        DIV("""/""".toRegex()),
        ADD("""\+""".toRegex()),
        SUB("""-""".toRegex()),
        EXCL_WS("""!(?=\s|$)""".toRegex()),
        EXCL_NO_WS("""!""".toRegex()),
        COLON(""":""".toRegex()),
        SEMICOLON(""";""".toRegex()),
        ASSIGNMENT("""=""".toRegex()),
        HASH("""#""".toRegex()),
        LANGLE("""<""".toRegex()),
        RANGLE(""">""".toRegex()),
        SINGLE_QUOTE("""'""".toRegex()),
        AMP("""&""".toRegex()),

        // Keywords (Match with word boundaries)
        RETURN_AT("""return@[a-zA-Z_][a-zA-Z0-9_]*""".toRegex()),
        CONTINUE_AT("""continue@[a-zA-Z_][a-zA-Z0-9_]*""".toRegex()),
        BREAK_AT("""break@[a-zA-Z_][a-zA-Z0-9_]*""".toRegex()),
        THIS_AT("""this@[a-zA-Z_][a-zA-Z0-9_]*""".toRegex()),
        SUPER_AT("""super@[a-zA-Z_][a-zA-Z0-9_]*""".toRegex()),

        FILE("""file\b""".toRegex()),
        FIELD("""field\b""".toRegex()),
        PROPERTY("""property\b""".toRegex()),
        GET("""get\b""".toRegex()),
        SET("""set\b""".toRegex()),
        RECEIVER("""receiver\b""".toRegex()),
        PARAM("""param\b""".toRegex()),
        SETPARAM("""setparam\b""".toRegex()), DELEGATE("""delegate\b""".toRegex()),
        PACKAGE("""package\b""".toRegex()),
        IMPORT("""import\b""".toRegex()), CLASS("""class\b""".toRegex()),
        INTERFACE("""interface\b""".toRegex()),
        FUN("""fun\b""".toRegex()), OBJECT("""object\b""".toRegex()),
        VAL("""val\b""".toRegex()),
        VAR("""var\b""".toRegex()),
        TYPE_ALIAS("""typealias\b""".toRegex()),
        CONSTRUCTOR("""constructor\b""".toRegex()),
        BY("""by\b""".toRegex()),
        COMPANION("""companion\b""".toRegex()),
        INIT("""init\b""".toRegex()),
        THIS("""this\b""".toRegex()),
        SUPER("""super\b""".toRegex()),
        TYPEOF("""typeof\b""".toRegex()),
        WHERE("""where\b""".toRegex()),
        IF("""if\b""".toRegex()),
        ELSE("""else\b""".toRegex()),
        WHEN("""when\b""".toRegex()),
        TRY("""try\b""".toRegex()),
        CATCH("""catch\b""".toRegex()),
        FINALLY("""finally\b""".toRegex()),
        FOR("""for\b""".toRegex()),
        DO("""do\b""".toRegex()),
        WHILE("""while\b""".toRegex()),
        THROW("""throw\b""".toRegex()),
        RETURN("""return\b""".toRegex()),
        CONTINUE("""continue\b""".toRegex()),
        BREAK("""break\b""".toRegex()),
        AS("""as\b""".toRegex()),
        IS("""is\b""".toRegex()),
        IN("""in\b""".toRegex()),
        OUT("""out\b""".toRegex()),
        DYNAMIC("""dynamic\b""".toRegex()),
        PUBLIC("""public\b""".toRegex()),
        PRIVATE("""private\b""".toRegex()),
        PROTECTED("""protected\b""".toRegex()),
        INTERNAL("""internal\b""".toRegex()),
        ENUM("""enum\b""".toRegex()),
        SEALED("""sealed\b""".toRegex()),
        ANNOTATION("""annotation\b""".toRegex()),
        DATA("""data\b""".toRegex()),
        INNER("""inner\b""".toRegex()),
        VALUE("""value\b""".toRegex()),
        TAILREC("""tailrec\b""".toRegex()),
        OPERATOR("""operator\b""".toRegex()),
        INLINE("""inline\b""".toRegex()),
        INFIX("""infix\b""".toRegex()),
        EXTERNAL("""external\b""".toRegex()),
        SUSPEND("""suspend\b""".toRegex()),
        OVERRIDE("""override\b""".toRegex()),
        ABSTRACT("""abstract\b""".toRegex()),
        FINAL("""final\b""".toRegex()),
        OPEN("""open\b""".toRegex()),
        CONST("""const\b""".toRegex()),
        LATEINIT("""lateinit\b""".toRegex()),
        VARARG("""vararg\b""".toRegex()),
        NOINLINE("""noinline\b""".toRegex()),
        CROSSINLINE("""crossinline\b""".toRegex()),
        REIFIED("""reified\b""".toRegex()),
        EXPECT("""expect\b""".toRegex()),
        ACTUAL("""actual\b""".toRegex()),

        // Literals
        NULL_LIT("""null\b""".toRegex()),
        BOOLEAN_LIT("""(true|false)\b""".toRegex()),
        UNSIGNED_LIT(UnsignedLiteral.toRegex()),
        LONG_LIT(LongLiteral.toRegex()),
        REAL_LIT(RealLiteral.toRegex()),
        HEX_LIT(HexLiteral.toRegex()),
        BIN_LIT(BinLiteral.toRegex()),
        INTEGER_LIT(IntegerLiteral.toRegex()),
        CHARACTER_LIT(CharacterLiteral.toRegex()),

        // Strings
        TRIPLE_QUOTE_OPEN(""""""""".toRegex()),
        QUOTE_OPEN(""""""".toRegex()),
        TRIPLE_QUOTE_CLOSE(""""""""".toRegex()), // Handled contextually
        QUOTE_CLOSE(""""""".toRegex()),

        // Identifiers
        FIELD_IDENTIFIER("""\$([a-zA-Z_][a-zA-Z0-9_]*|`[^`\r\n]+`)""".toRegex()), // e.g. $foo
        IDENTIFIER("""[a-zA-Z_\p{L}][a-zA-Z0-9_\p{L}\p{Nd}]*|`[^`\r\n]+`""".toRegex()), // Includes Unicode letters/digits

        // LineString / MultiLineString specific
        LINE_STR_EXPR_START("""\$\{""".toRegex()),
        LINE_STR_ESCAPED_CHAR("""\\([tbrn'"\\$]|u[0-9a-fA-F]{4})""".toRegex()),
        LINE_STR_TEXT("""[^\\"${'$'}]+""".toRegex()),

        MULTILINE_STR_EXPR_START("""\$\{""".toRegex()),
        MULTILINE_STR_TEXT("""[^"${'$'}]+""".toRegex()),
        MULTILINE_STRING_QUOTE(""""+""".toRegex()),

        // Trivia
        NL("""\r?\n""".toRegex()),
        EOF("""\z""".toRegex()),
        ERROR_CHARACTER(""".""".toRegex()),
    }

    @Suppress("ConstPropertyName")
    companion object {
        // Spec-compliant literal Regexes
        private const val DecDigitNoZero = "[1-9]"
        private const val DecDigit = "[0-9]"
        private const val DecDigits = "$DecDigit(?:[0-9_]*$DecDigit)?"
        private const val DoubleExponent = "[eE][+-]?$DecDigits"

        const val IntegerLiteral = "(?:$DecDigitNoZero(?:[0-9_]*$DecDigit)?|0)(?!\\.)"
        const val HexLiteral = "0[xX][0-9a-fA-F](?:[0-9a-fA-F_]*[0-9a-fA-F])?"
        const val BinLiteral = "0[bB][01](?:[01_]*[01])?"
        const val LongLiteral = "(?:$IntegerLiteral|$HexLiteral|$BinLiteral)[lL]"
        const val UnsignedLiteral = "(?:$IntegerLiteral|$HexLiteral|$BinLiteral)[uU][lL]?"

        private const val DoubleLiteral =
            "(?:$DecDigits\\.$DecDigits(?:$DoubleExponent)?|$DecDigits$DoubleExponent)"
        private const val FloatLiteral = "(?:$DoubleLiteral[fF]|$DecDigits[fF])"
        const val RealLiteral = "(?:$FloatLiteral|$DoubleLiteral)"

        const val CharacterLiteral = "'(?:\\\\[tbrn'\"\\\\\$]|\\\\u[0-9a-fA-F]{4}|[^\\\\\\n\\r'])'"
    }
}
