package com.jalloft.compilador.com.jalloft.compilador.errors

import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.token.Lexical

data class LexicalError(
    val type: ErrorType = ErrorType.LexicalError,
    val message: String,
    private val line: Int
) : Lexical {
    override fun line(): Int = line
}
