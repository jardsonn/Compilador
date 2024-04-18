package com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer

import kotlin.math.max
import kotlin.math.min

class Lexer(private val source: String) {

    private var hasNextPosition = 0

    private var currentPosition = 0
        set(value) {
            hasNextPosition = value
            field = value
        }

    fun hasNext(): Boolean {
        val hasNext = hasNextPosition <= source.length
        if (hasNextPosition == source.length){
            hasNextPosition++
        }
        return hasNext
    }


    fun getNextToken(): TokenBase {

        skipWhitespace()

        val isSkippedComment = skipComment()

        if (!isSkippedComment) {
            return TokenBase.UnformedToken("Erro Lexical: Comentário multilinha não fechado.")
        }

        val currentChar = source[min(currentPosition, source.lastIndex)]

        val token: TokenBase = when {
            currentChar.isLetter() -> scanIdentifierOrKeyword()
            currentChar.isDigit() || currentChar.isNextNegativeDigit() -> scanNumber()
            currentChar in SPECIAL_SYMBOLS -> scanSpecialSymbol()
            else -> {
                currentPosition++
                return TokenBase.UnformedToken("Erro Lexical: Símbolo $currentChar é desconhecido.")

            }
        }

        return token
    }


    private fun scanIdentifierOrKeyword(): TokenBase {
        val startIndex = currentPosition
        var state = 0
        var isError = false
        while (currentPosition < source.length) {
            val char = source[currentPosition]
            when (state) {
                0 -> {
                    if (char.isLetter()) {
                        state = 1
                    } else {
                        break
                    }
                }

                1 -> {
                    state = if (char.isLetterOrDigit()) {
                        1
                    } else if (char == '_') {
                        2
                    } else if (char == '@') {
                        4
                    } else {
                        break
                    }
                }

                2 -> {
                    if (char.isLetterOrDigit()) {
                        state = 3
                    } else {
                        break
                    }
                }

                3 -> {
                    if (char.isLetterOrDigit()) {
                        state = 3
                    } else if (char == '_') {
                        state = 2
                    } else if (char == '@') {
                        isError = true
                    } else {
                        break
                    }
                }

                4 -> {
                    if (char.isLetterOrDigit()) {
                        state = 5
                    } else {
                        break
                    }
                }

                5 -> {
                    if (char.isLetterOrDigit()) {
                        state = 5
                    } else if (char == '@') {
                        state = 4
                    } else if (char == '_') {
                        isError = true
                    } else {
                        break
                    }
                }

            }
            currentPosition++
        }

        val lexeme = source.substring(startIndex, currentPosition)

        if (isError) {
            return TokenBase.UnformedToken("Erro Lexical: Identificador $lexeme é inválido.")

        }

        return if (state == 1) {
            if (lexeme in KEYWORDS) {
                TokenBase.Token(Tokens.KEYWORD, lexeme)
            } else {
                TokenBase.Token(Tokens.IDENTIFIER, lexeme)
            }
        } else if (state == 3 || state == 5) {
            TokenBase.Token(Tokens.IDENTIFIER, lexeme)
        } else {
            return TokenBase.UnformedToken("Erro Lexical: Identificador $lexeme é inválido.")
        }
    }


    private fun scanNumber(): TokenBase {
        val startIndex = currentPosition
        var state = 0

        while (currentPosition < source.length) {
            val char = source[currentPosition]
            when (state) {
                0 -> {
                    state = when {
                        char.isDigit() -> 1
                        char == '-' -> 2
                        else -> break
                    }
                }

                1 -> {
                    state = when {
                        char.isDigit() -> 1
                        char == ',' -> 3
                        else -> break
                    }
                }

                2 -> {
                    state = if (char.isDigit()) {
                        1
                    } else {
                        break
                    }
                }

                3 -> {
                    state = if (char.isDigit()) {
                        4
                    } else {
                        break
                    }
                }

                4 -> {
                    state = if (char.isDigit()) {
                        4
                    } else {
                        break
                    }
                }
            }
            currentPosition++
        }

        val lexeme = source.substring(startIndex, currentPosition)
        return when (state) {
            1 -> TokenBase.Token(Tokens.INTEGER_NUMBER, lexeme)
            4 -> TokenBase.Token(Tokens.DECIMAL_NUMBER, lexeme)
            else -> TokenBase.UnformedToken("Erro Lexical: Formato de número inválido")


        }
    }


    private fun scanSpecialSymbol(): TokenBase {
        val startIndex = currentPosition
        var state = 0
        while (currentPosition < source.length) {
            val char = source[currentPosition]
            when (state) {
                0 -> {
                    state = if (char == '>' || char == '-' || char == ':') {
                        1
                    } else if (char == '<') {
                        2
                    } else if (char in SPECIAL_SYMBOLS) {
                        3
                    } else {
                        break
                    }
                }

                1 -> {
                    if (char == '=') {
                        state = 3
                    } else {
                        break
                    }
                }

                2 -> {
                    if (char == '=' || char == '>') {
                        state = 3
                    } else {
                        break
                    }
                }

                3 -> break
            }
            currentPosition++
        }
        val lexeme = source.substring(startIndex, currentPosition)
        return if (state != 0) {
            TokenBase.Token(Tokens.entries.find { it.symbol == lexeme } ?: Tokens.SPECIAL_SYMBOL, lexeme)
        } else {
            TokenBase.UnformedToken("Erro Lexical: Símbolo $lexeme é desconhecido.")
        }

    }

    private fun skipComment(): Boolean {
        var state = 0
        if (source.getOrNull(currentPosition) == '!') {
            while (currentPosition < source.length) {
                val char = source[currentPosition]
                when (state) {
                    0 -> {
                        if (char == '!') {
                            state = 1
                        } else {
                            break
                        }
                    }

                    1 -> {
                        state = if (char == '!') {
                            6
                        } else if (char.isLineBreak()) {
                            3
                        } else {
                            2
                        }
                    }

                    2 -> {
                        state = if (char.isLineBreak()) {
                            3
                        } else {
                            2
                        }
                    }

                    3 -> {
                        skipWhitespace()
                        break
                    }

                    4 -> {
                        state = if (char == '!') {
                            3
                        } else {
                            5
                        }
                    }

                    5 -> {
                        state = if (char == '!') {
                            4
                        } else {
                            5
                        }
                    }

                    6 -> {
                        state = if (char == '!') {
                            4
                        } else {
                            5
                        }
                    }
                }
                currentPosition++
            }
        } else {
            return true
        }

        return state == 3
    }


    private fun skipWhitespace() {
        while (currentPosition < source.length && source[currentPosition].isWhitespace()) {
            currentPosition++
        }
    }

    private fun Char.isLineBreak() = this == '\n' || this == '\r'

    private fun Char.isNextNegativeDigit(): Boolean {
        return (this == '-' && (currentPosition + 1) < source.length && source[currentPosition + 1].isDigit())
    }

    companion object {
        private val KEYWORDS = setOf(
            "principal",
            "if",
            "then",
            "else",
            "while",
            "do",
            "until",
            "repeat",
            "int",
            "double",
            "char",
            "case",
            "switch",
            "end",
            "procedure",
            "function",
            "for",
            "begin"
        )
        private val SPECIAL_SYMBOLS = setOf(
            ';', ',', '.', '+', '*', '(', ')', '=', '{', '}', '/', '@', '>', '<', ':', '-',
        )
    }
}
