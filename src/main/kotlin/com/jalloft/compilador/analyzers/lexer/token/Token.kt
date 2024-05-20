package com.jalloft.compilador.analyzers.lexer.token


data class Token(
    val token: Tokens,
    val lexeme: String,
    val line: Int
) : Lexical

