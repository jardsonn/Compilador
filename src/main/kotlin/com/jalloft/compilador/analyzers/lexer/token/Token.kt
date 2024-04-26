package com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.token

data class Token(
    val classification: TokenClassifications, val lexeme: String, private val line: Int
) : Lexical {
    override fun line(): Int = line
}
