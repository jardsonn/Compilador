package com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer

import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.states.CommentStates
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.states.DigitStates
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.states.IdentifiersKeywordStates
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.states.SymbolStates
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.token.Lexical
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.token.Token
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.token.TokenClassifications
import com.jalloft.compilador.com.jalloft.compilador.errors.ErrorType
import com.jalloft.compilador.com.jalloft.compilador.utils.exitProcessWithError
import kotlin.properties.Delegates

class Tokenizer(private val source: String) {

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


    fun getNextToken(): Token? {
        if (source.trim().isEmpty()) {
            exitProcessWithError(LexicalError(message = "Arquivo vazio", line = currentLine))
        }

        skipWhitespace()
        scanComment()

        val char = source.getOrNull(currentPosition) ?: return null

        val lexical: Lexical = when {
            char.isLetter() -> scanIdentifierOrKeyword()
            char.isDigit() || char.isNextNegativeDigit() -> scanNumber()
            char in SPECIAL_SYMBOLS -> scanSpecialSymbol()
            else -> {
                LexicalError(
                    message = " Símbolo \"$char\" é desconhecido.",
                    line = currentLine
                )
            }
        }

        if (lexical is LexicalError) {
            exitProcessWithError(lexical)
        }

        return lexical as Token
    }


    private fun scanIdentifierOrKeyword(): Lexical {
        val startIndex = currentPosition
        var state = IdentifiersKeywordStates.STATE_0

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
                    if (char.isLetterOrDigit()) {
                        state = IdentifiersKeywordStates.STATE_2
                    } else if (char == '_') {
                        state = IdentifiersKeywordStates.STATE_3
                    } else if (char == '@') {
                        state = IdentifiersKeywordStates.STATE_5
                    } else {
                        break
                    }
                }

                IdentifiersKeywordStates.STATE_2 -> {
                    if (char.isLetterOrDigit()) {
                        state = IdentifiersKeywordStates.STATE_2
                    } else {
                        break
                    }
                }

                IdentifiersKeywordStates.STATE_3 -> {
                    if (char.isLetterOrDigit()) {
                        state = IdentifiersKeywordStates.STATE_4
                    } else {
                        break
                    }
                }

                IdentifiersKeywordStates.STATE_4 -> {
                    if (char.isLetterOrDigit()) {
                        state = IdentifiersKeywordStates.STATE_4
                    } else if (char == '_') {
                        state = IdentifiersKeywordStates.STATE_3
                    } else {
                        break
                    }
                }

                IdentifiersKeywordStates.STATE_5 -> {
                    if (char.isLetterOrDigit()) {
                        state = IdentifiersKeywordStates.STATE_6
                    } else {
                        break
                    }
                }

                IdentifiersKeywordStates.STATE_6 -> {
                    if (char.isLetterOrDigit()) {
                        state = IdentifiersKeywordStates.STATE_6
                    } else if (char == '@') {
                        state = IdentifiersKeywordStates.STATE_5
                    } else {
                        break
                    }
                }
            }
            nextChar()
        }

        val lexeme = source.substring(startIndex, currentPosition)


        return if (state == IdentifiersKeywordStates.STATE_1 || state == IdentifiersKeywordStates.STATE_4 || state == IdentifiersKeywordStates.STATE_6) {
            Token(TokenClassifications.IDENTIFIER, lexeme, currentLine)
        } else if (state == IdentifiersKeywordStates.STATE_2) {
            if (lexeme in KEYWORDS) {
                Token(TokenClassifications.KEYWORD, lexeme, currentLine)
            } else {
                Token(TokenClassifications.IDENTIFIER, lexeme, currentLine)
            }
        } else {
            LexicalError(message = "Identificador \"$lexeme\" é inválido.", line = currentLine)
        }
    }


    private fun scanNumber(): Lexical {
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
            nextChar()
        }

        val lexeme = source.substring(startIndex, currentPosition)
        return when (state) {
            DigitStates.STATE_1 -> Token(TokenClassifications.INTEGER_NUMBER, lexeme, currentLine)
            DigitStates.STATE_4 -> Token(TokenClassifications.DECIMAL_NUMBER, lexeme, currentLine)
            else -> LexicalError(message = "Formato de número inválido.", line = currentLine)
        }
    }


    private fun scanSpecialSymbol(): Lexical {
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
            nextChar()
        }
        val lexeme = source.substring(startIndex, currentPosition)
        return if (state != SymbolStates.STATE_0) {
//            TokenBase.Token(Tokens.entries.find { it.symbol == lexeme } ?: Tokens.SPECIAL_SYMBOL, lexeme, currentLine)
            Token(TokenClassifications.SPECIAL_SYMBOL, lexeme, currentLine)
        } else {
            LexicalError(message = "Símbolo $lexeme é desconhecido.", line = currentLine)
        }

    }

    private fun scanComment() {
        val commentLine = currentLine
        if (source.getOrNull(currentPosition).isComment()) {
            var state = CommentStates.STATE_0
            while (currentPosition < source.length) {
                var char = source[currentPosition]
                when (state) {
                    CommentStates.STATE_0 -> {
                        if (char == '@') {
                            state = CommentStates.STATE_1
                        } else if (char == '/') {
                            state = CommentStates.STATE_3
                        } else if (char == '!') {
                            state = CommentStates.STATE_4
                        } else {
                            break
                        }
                    }

                    CommentStates.STATE_1 -> {
                        if (char == '@') {
                            state = CommentStates.STATE_2
                        } else {
                            break
                        }
                    }

                    CommentStates.STATE_2 -> {
                        if (char.isLineBreak()) {
                            state = CommentStates.STATE_9
                        } else {
                            state = CommentStates.STATE_2
                        }
                    }

                    CommentStates.STATE_3 -> {
                        if (char == '/') {
                            state = CommentStates.STATE_7
                        } else {
                            break
                        }
                    }

                    CommentStates.STATE_4 -> {
                        if (char == '!') {
                            state = CommentStates.STATE_5
                        } else if (char.isLineBreak()) {
                            state = CommentStates.STATE_9
                        } else {
                            state = CommentStates.STATE_2
                        }
                    }

                    CommentStates.STATE_5 -> {
                        if (char == '!') {
                            state = CommentStates.STATE_6
                        } else {
                            state = CommentStates.STATE_5
                        }
                    }

                    CommentStates.STATE_6 -> {
                        if (char == '!') {
                            state = CommentStates.STATE_9
                        } else {
                            state = CommentStates.STATE_5
                        }
                    }

                    CommentStates.STATE_7 -> {
                        if (char == '/') {
                            state = CommentStates.STATE_8
                        } else {
                            state = CommentStates.STATE_7
                        }
                    }

                    CommentStates.STATE_8 -> {
                        if (char == '/') {
                            state = CommentStates.STATE_9
                        } else {
                            state = CommentStates.STATE_7
                        }
                    }

                    CommentStates.STATE_9 -> {
                        skipWhitespace()
                        val currentChar = source.getOrNull(currentPosition)
                        if (currentChar == '!') {
                            state = CommentStates.STATE_4
                        } else if (currentChar == '@') {
                            state = CommentStates.STATE_1
                        } else if (currentChar == '/') {
                            state = CommentStates.STATE_3
                        } else {
                            break
                        }
                    }
                }
                nextChar()
            }

            if (state == CommentStates.STATE_1 || state == CommentStates.STATE_3
                || (state != CommentStates.STATE_9 && !isEOF())
            ) {
                // Trata-se de um símbolo especial, portanto, é necessário
                // retroceder um caractere para que seja reconhecido pelo
                // autômato de símbolos especiais.
                previousChar()
            }

            if (state != CommentStates.STATE_1 && state != CommentStates.STATE_3 && state != CommentStates.STATE_9 && !isEOF() || state == CommentStates.STATE_5 || state == CommentStates.STATE_6 || state == CommentStates.STATE_7 || state == CommentStates.STATE_8) {
                exitProcessWithError(
                    LexicalError(
                        ErrorType.LexicalError,
                        "Comentário multilinha não fechado",
                        commentLine
                    )
                )
            }
        }
    }


    private fun skipWhitespace() {
        while (currentPosition < source.length && source[currentPosition].isWhitespace()) {
            countLine()
            nextChar()
        }
    }

    private fun countLine() {
        if (source[currentPosition].isLineBreak()) {
            currentLine++
        }
    }

    private fun nextChar() {
        currentPosition++
    }

    private fun previousChar() {
        currentPosition--
    }


    private fun Char.isLineBreak() = this == '\n' || this == '\r'

    private fun Char.isNextNegativeDigit(): Boolean {
        return (this == '-' && (currentPosition + 1) < source.length && source[currentPosition + 1].isDigit())
    }

    private fun Char?.isComment() = this == '@' || this == '/' || this == '!'

    private fun isEOF(): Boolean = currentPosition >= source.lastIndex


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
