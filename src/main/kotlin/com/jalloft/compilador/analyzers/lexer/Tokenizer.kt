package com.jalloft.compilador.analyzers.lexer

import com.google.common.io.Resources
import com.jalloft.compilador.analyzers.lexer.states.CommentStates
import com.jalloft.compilador.analyzers.lexer.states.DigitStates
import com.jalloft.compilador.analyzers.lexer.states.IdentifiersKeywordStates
import com.jalloft.compilador.analyzers.lexer.states.SymbolStates
import com.jalloft.compilador.analyzers.lexer.token.Lexical
import com.jalloft.compilador.analyzers.lexer.token.Token
import com.jalloft.compilador.analyzers.lexer.token.TokenType
import com.jalloft.compilador.analyzers.lexer.token.Tokens
import com.jalloft.compilador.errors.ErrorType
import com.jalloft.compilador.utils.exitProcessWithError

class Tokenizer(private val sourcePath: String) {

    private var currentLine = 1

    private var hasNextPosition = 0

    private var currentPosition = 0
        set(value) {
            hasNextPosition = value
            field = value
        }

    private val source by lazy { Resources.getResource(sourcePath).readText() }

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
            Token(Tokens.IDENTIFIER, lexeme, currentLine)
        } else if (state == IdentifiersKeywordStates.STATE_2) {
            if (lexeme in KEYWORDS) {
                Token(Tokens.entries.find { it.name.lowercase() == lexeme } ?: Tokens.IDENTIFIER, lexeme, currentLine)
            } else {
                Token(Tokens.IDENTIFIER, lexeme, currentLine)
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
                        char == '.' -> DigitStates.STATE_3
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
        return if (state == DigitStates.STATE_1 || state == DigitStates.STATE_4) {
            Token(Tokens.NUMBER, lexeme, currentLine)
        } else {
            LexicalError(message = "Formato de número inválido.", line = currentLine)
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
            val symbol = Tokens.entries.find { it.content == lexeme }
            if (symbol != null){
                Token(symbol, lexeme, currentLine)
            }else{
                LexicalError(message = "Símbolo $lexeme é desconhecido.", line = currentLine)
            }
        } else {
            LexicalError(message = "Símbolo $lexeme é desconhecido.", line = currentLine)
        }

    }

    private fun scanComment() {
        val commentLine = currentLine
        var state = CommentStates.STATE_0
        while (currentPosition < source.length) {
            val char = source[currentPosition]
            when (state) {
                CommentStates.STATE_0 -> {
                    if (char == '@') {
                        state = CommentStates.STATE_1
                        if (source.getOrNull(currentPosition + 1) != '@') {
                            break
                        }
                    } else if (char == '/') {
                        state = CommentStates.STATE_3
                        if (source.getOrNull(currentPosition + 1) != '/') {
                            break
                        }
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
                        if (source.getOrNull(currentPosition + 1) != '@') {
                            break
                        }
                    } else if (currentChar == '/') {
                        state = CommentStates.STATE_3
                        if (source.getOrNull(currentPosition + 1) != '/') {
                            break
                        }
                    } else {
                        break
                    }
                }
            }
            nextChar()
        }

        if (state == CommentStates.STATE_0 || state == CommentStates.STATE_1 || state == CommentStates.STATE_3) {
            return
        } else if ((state != CommentStates.STATE_9)) {
            exitProcessWithError(
                LexicalError(
                    ErrorType.LexicalError,
                    "Comentário multilinha não fechado",
                    commentLine
                )
            )
        }
    }


    private fun skipWhitespace() {
        while (currentPosition < source.length && source[currentPosition].isWhitespace()) {
            nextChar()
        }
    }

    private fun nextChar() {
        if (source[currentPosition] == '\n' || (source[currentPosition] == '\r' && source.getOrNull(currentPosition + 1) != '\n')) {
            currentLine++
        }
        currentPosition++
    }

    private fun Char.isLineBreak() = this == '\n' || this == '\r'

    private fun Char.isNextNegativeDigit(): Boolean {
        return (this == '-' && (currentPosition + 1) < source.length && source[currentPosition + 1].isDigit())
    }

    private fun isEOF(): Boolean = currentPosition >= source.lastIndex


    companion object {
        private val KEYWORDS = Tokens.entries.filter { it.type == TokenType.RESERVED_WORD }.map { it.content }.toSet()
        private val SPECIAL_SYMBOLS = setOf(
            ';', ',', '.', '+', '*', '(', ')', '=', '{', '}', '/', '@', '>', '<', ':', '-',
        )
    }
}

