package com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.token


sealed class TokenBase(val line: Int) {
    data class Token(val classification: Tokens, val lexeme: String, val tokenLine: Int) : TokenBase(tokenLine)
    data class UnformedToken(val errorType: String, val errorMessage: String, val errorLine: Int) : TokenBase(errorLine)
}


