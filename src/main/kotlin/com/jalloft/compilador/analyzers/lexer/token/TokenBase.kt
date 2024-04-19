package com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.token


sealed class TokenBase {
    data class Token(val classification: Tokens, val lexeme: String) : TokenBase()
    data class UnformedToken(val errorMessage: String) : TokenBase()
}


