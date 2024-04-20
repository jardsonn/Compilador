package com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer

import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.states.CommentStates
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.states.DigitStates
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.states.IdentifiersKeywordStates
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.states.SymbolStates
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.token.TokenBase
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.token.Tokens
import kotlin.math.min

class Lexer(private val source: String) {

    private var currentLine = 1

    private var hasNextPosition = 0

    private var currentPosition = 0
        set(value) {
            hasNextPosition = value
            field = value
        }

    fun hasNext(): Boolean {
        val hasNext = hasNextPosition <= source.length
        if (hasNextPosition == source.length) {
            hasNextPosition++
        }
        return hasNext
    }


    fun getNextToken(): TokenBase {

        skipWhitespace()

        val isSkippedComment = skipComment()

        if (!isSkippedComment) {
            return TokenBase.UnformedToken("Erro Lexical","Comentário multilinha não fechado.", currentLine)
        }

        if (source.trim().isEmpty()){
            return TokenBase.UnformedToken("Erro Lexical", "Arquivo vazio", currentLine)
        }

        val currentChar = source[min(currentPosition, source.lastIndex)]

        val token: TokenBase = when {
            currentChar.isLetter() -> scanIdentifierOrKeyword()
            currentChar.isDigit() || currentChar.isNextNegativeDigit() -> scanNumber()
            currentChar in SPECIAL_SYMBOLS -> scanSpecialSymbol()
            else -> {
                currentPosition++
                return TokenBase.UnformedToken("Erro Lexical"," Símbolo $currentChar é desconhecido.", currentLine)
            }
        }

        return token
    }


    private fun scanIdentifierOrKeyword(): TokenBase {
        val startIndex = currentPosition
        var state = IdentifiersKeywordStates.STATE_0
        var isError = false
        while (currentPosition < source.length) {
            val char = source[currentPosition]
            when (state) {
                IdentifiersKeywordStates.STATE_0 -> {
                    if (char.isLetter()) {
                        state = IdentifiersKeywordStates.STATE_1
                    } else {
                        break
                    }
                }

                IdentifiersKeywordStates.STATE_1 -> {
                    state = if (char.isLetterOrDigit()) {
                        IdentifiersKeywordStates.STATE_1
                    } else if (char == '_') {
                        IdentifiersKeywordStates.STATE_2
                    } else if (char == '@') {
                        IdentifiersKeywordStates.STATE_4
                    } else {
                        break
                    }
                }

                IdentifiersKeywordStates.STATE_2 -> {
                    if (char.isLetterOrDigit()) {
                        state = IdentifiersKeywordStates.STATE_3
                    } else {
                        break
                    }
                }

                IdentifiersKeywordStates.STATE_3 -> {
                    state = if (char.isLetterOrDigit()) {
                        IdentifiersKeywordStates.STATE_3
                    } else if (char == '_') {
                        IdentifiersKeywordStates.STATE_2
                    } else {
                        break
                    }
                }

                IdentifiersKeywordStates.STATE_4 -> {
                    if (char.isLetterOrDigit()) {
                        state = IdentifiersKeywordStates.STATE_5
                    } else {
                        break
                    }
                }

                IdentifiersKeywordStates.STATE_5 -> {
                    if (char.isLetterOrDigit()) {
                        state = IdentifiersKeywordStates.STATE_5
                    } else if (char == '@') {
                        state = IdentifiersKeywordStates.STATE_4
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
            return TokenBase.UnformedToken("Erro Lexical","Identificador $lexeme é inválido.", currentLine)
        }

        return if (state == IdentifiersKeywordStates.STATE_1) {
            if (lexeme in KEYWORDS) {
                TokenBase.Token(Tokens.KEYWORD, lexeme, currentLine)
            } else {
                TokenBase.Token(Tokens.IDENTIFIER, lexeme, currentLine)
            }
        } else if (state == IdentifiersKeywordStates.STATE_3 || state == IdentifiersKeywordStates.STATE_5) {
            TokenBase.Token(Tokens.IDENTIFIER, lexeme, currentLine)
        } else {
            return TokenBase.UnformedToken("Erro Lexical","Identificador $lexeme é inválido.", currentLine)
        }
    }


    private fun scanNumber(): TokenBase {
        val startIndex = currentPosition
        var state = DigitStates.STATE_0

        while (currentPosition < source.length) {
            val char = source[currentPosition]
            when (state) {
                DigitStates.STATE_0 -> {
                    state = when {
                        char.isDigit() -> DigitStates.STATE_1
                        char == '-' -> DigitStates.STATE_2
                        else -> break
                    }
                }

                DigitStates.STATE_1 -> {
                    state = when {
                        char.isDigit() -> DigitStates.STATE_1
                        char == ',' -> DigitStates.STATE_3
                        else -> break
                    }
                }

                DigitStates.STATE_2 -> {
                    state = if (char.isDigit()) {
                        DigitStates.STATE_1
                    } else {
                        break
                    }
                }

                DigitStates.STATE_3 -> {
                    state = if (char.isDigit()) {
                        DigitStates.STATE_4
                    } else {
                        break
                    }
                }

                DigitStates.STATE_4 -> {
                    state = if (char.isDigit()) {
                        DigitStates.STATE_4
                    } else {
                        break
                    }
                }
            }
            currentPosition++
        }

        val lexeme = source.substring(startIndex, currentPosition)
        return when (state) {
            DigitStates.STATE_1 -> TokenBase.Token(Tokens.INTEGER_NUMBER, lexeme, currentLine)
            DigitStates.STATE_4 -> TokenBase.Token(Tokens.DECIMAL_NUMBER, lexeme, currentLine)
            else -> TokenBase.UnformedToken("Erro Lexical","Formato de número inválido.", currentLine)


        }
    }


    private fun scanSpecialSymbol(): TokenBase {
        val startIndex = currentPosition
        var state = SymbolStates.STATE_0
        while (currentPosition < source.length) {
            val char = source[currentPosition]
            when (state) {
                SymbolStates.STATE_0 -> {
                    state = if (char == '>' || char == '-' || char == ':') {
                        SymbolStates.STATE_1
                    } else if (char == '<') {
                        SymbolStates.STATE_2
                    } else if (char in SPECIAL_SYMBOLS) {
                        SymbolStates.STATE_3
                    } else {
                        break
                    }
                }

                SymbolStates.STATE_1 -> {
                    if (char == '=') {
                        state = 3
                    } else {
                        break
                    }
                }

                SymbolStates.STATE_2 -> {
                    if (char == '=' || char == '>') {
                        state = SymbolStates.STATE_3
                    } else {
                        break
                    }
                }

                SymbolStates.STATE_3 -> break
            }
            currentPosition++
        }
        val lexeme = source.substring(startIndex, currentPosition)
        return if (state != SymbolStates.STATE_0) {
//            TokenBase.Token(Tokens.entries.find { it.symbol == lexeme } ?: Tokens.SPECIAL_SYMBOL, lexeme, currentLine)
            TokenBase.Token(Tokens.SPECIAL_SYMBOL, lexeme, currentLine)
        } else {
            TokenBase.UnformedToken("Erro Lexical","Símbolo $lexeme é desconhecido.", currentLine)
        }

    }

    private fun skipComment(): Boolean {
        var state = CommentStates.STATE_0
        if (source.getOrNull(currentPosition) == '!') {
            while (currentPosition < source.length) {
                val char = source[currentPosition]
                when (state) {
                    CommentStates.STATE_0 -> {
                        if (char == '!') {
                            state = CommentStates.STATE_1
                        } else {
                            break
                        }
                    }

                    CommentStates.STATE_1 -> {
                        state = if (char == '!') {
                            CommentStates.STATE_6
                        } else if (char.isLineBreak()) {
                            CommentStates.STATE_3
                        } else {
                            CommentStates.STATE_2
                        }
                    }

                    CommentStates.STATE_2 -> {
                        state = if (char.isLineBreak()) {
                            CommentStates.STATE_3
                        } else {
                            CommentStates.STATE_2
                        }
                    }

                    CommentStates.STATE_3 -> {
                        skipWhitespace()
                        break
                    }

                    CommentStates.STATE_4 -> {
                        state = if (char == '!') {
                            CommentStates.STATE_3
                        } else {
                            CommentStates.STATE_5
                        }
                    }

                    CommentStates.STATE_5 -> {
                        state = if (char == '!') {
                            CommentStates.STATE_4
                        } else {
                            CommentStates.STATE_5
                        }
                    }

                    CommentStates.STATE_6 -> {
                        state = if (char == '!') {
                            CommentStates.STATE_4
                        } else {
                            CommentStates.STATE_5
                        }
                    }
                }
                currentPosition++
            }
        } else {
            return true
        }

        return state == CommentStates.STATE_3
    }


    private fun skipWhitespace() {
        while (currentPosition < source.length && source[currentPosition].isWhitespace()) {
            countLine()
            currentPosition++
        }
    }

    private fun countLine() {
        if (source[currentPosition].isLineBreak()) {
            currentLine++
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
