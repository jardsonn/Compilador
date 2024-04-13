package com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer

class Lexer(private val source: String) {

    private var currentPosition = 0

    fun hasNext(): Boolean {
        return currentPosition < source.length
    }

    fun getNextToken(): Token? {

        skipWhitespace()

        skipComment()

        val currentChar = source.getOrNull(currentPosition) ?: return null

        val token: Token?
        when {
            currentChar.isLetter() -> token = scanIdentifierOrKeyword()
            currentChar.isDigit() || isMinusSign(currentChar) -> token = scanNumber()
            currentChar in SPECIAL_SYMBOLS -> token = scanSpecialSymbol()
            else -> {
                currentPosition++
                token = Token(Tokens.UNKNOWN, currentChar.toString(), "Símbolo não reconhecido")
            }
        }

        return token
    }


    private fun isMinusSign(char: Char): Boolean {
        return (char == '-' && (currentPosition + 1) < source.length && source[currentPosition + 1].isDigit())
    }

    private fun scanIdentifierOrKeyword(): Token {
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
                    } else if (char == '@') {// perguntar para a professora
                        isError = true
//                        break
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
                    } else if (char == '_') {// perguntar para a professora
                        isError = true
//                        break
                    } else {
                        break
                    }
                }

            }
            currentPosition++
        }

        val lexeme = source.substring(startIndex, currentPosition)

        if (isError) {
            return Token(Tokens.IDENTIFIER, lexeme, "Identificador inválido")
        }

        return if (state == 1) {
            if (lexeme in KEYWORDS) {
                Token(Tokens.KEYWORD, lexeme)
            } else {
                Token(Tokens.IDENTIFIER, lexeme)
            }
        } else if (state == 3 || state == 5) {
            Token(Tokens.IDENTIFIER, lexeme)
        } else {
//            throw LexicalErrorException("$lexeme não é um identificador válido")
            return Token(Tokens.IDENTIFIER, lexeme, "Identificador inválido")
        }
    }


    private fun scanNumber(): Token {
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
            1 -> Token(Tokens.INTEGER_NUMBER, lexeme)
            4 -> Token(Tokens.DECIMAL_NUMBER, lexeme)
//            else -> throw LexicalErrorException("O $lexeme é um símbolo não reconhecido")
            else -> Token(Tokens.UNKNOWN, lexeme, "Formato de número inválido")

        }
    }


    private fun scanSpecialSymbol(): Token {
        val startIndex = currentPosition
        var state = 0
        while (currentPosition < source.length) {
            val char = source[currentPosition]
            when (state) {
                0 -> {
                    if (char == '>' || char == '-' || char == ':') {
                        state = 1
                    } else if (char == '<') {
                        state = 2
                    } else if (char in SPECIAL_SYMBOLS) {
                        state = 3
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
        if (state != 0) {
            return Token(Tokens.entries.find { it.symbol == lexeme } ?: Tokens.SPECIAL_SYMBOL, lexeme)
        } else {
            return Token(Tokens.UNKNOWN, lexeme, "Símbolo não reconhecido")
        }

    }

    private fun skipComment() {
        var state = 0
        if (source.getOrNull(currentPosition) == '!') {
            while (currentPosition < source.length) {
//                skipWhitespace()
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
                        if (char == '!') {
                            state = 6
                        } else if (char.isLineBreak()) {
                            state = 3
                        } else if (char.isLetterOrDigit() || char in SPECIAL_SYMBOLS || char.isWhitespace()) {
                            state = 2
                        } else {
                            break
                        }
                    }

                    2 -> {
                        if (char.isLineBreak()) {
                            state = 3
                        } else if (char.isLetterOrDigit() || char in SPECIAL_SYMBOLS || char == '!' || char.isWhitespace()) {
                            state = 2
                        } else {
                            break
                        }
                    }

                    3 -> {
                        skipWhitespace()
                        break
                    }

                    4 -> {
                        state = if (char == '!') {
                            3
                        } else if (char.isLetterOrDigit() || char in SPECIAL_SYMBOLS || char.isWhitespace()) {
                            5
                        } else {
                            break
                        }
                    }

                    5 -> {
                        state = if (char.isLetterOrDigit() || char.isLineBreak() || char in SPECIAL_SYMBOLS || char.isWhitespace()) {
                            5
                        } else if (char == '!') {
                            4
                        } else {
                            break
                        }
                    }

                    6 -> {
                        state = if (char == '!') {
                            4
                        } else if (char.isLetterOrDigit() || char in SPECIAL_SYMBOLS || char.isWhitespace()) {
                            5
                        } else {
                            break
                        }
                    }
                }
                currentPosition++
            }
        }
    }


    private fun skipWhitespace() {
        while (currentPosition < source.length && source[currentPosition].isWhitespace()) {
            currentPosition++
        }
    }

    private fun Char.isLineBreak() = this == '\n' || this == '\r'


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
            ';', ',', '.', '+', '*', '(', ')', '=', '{', '}', '/', '@', /*'_',*/ /*'"',*/ /*'\'',*/ '>', '<', ':', '-',
        )
    }
}