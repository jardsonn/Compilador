package com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer

import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.token.Lexical
import com.jalloft.compilador.com.jalloft.compilador.errors.ErrorType

data class LexicalError(
    val type: ErrorType = ErrorType.LexicalError,
    val message: String,
    private val line: Int
) : Lexical {
    override fun line(): Int = line
}
