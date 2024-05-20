package com.jalloft.compilador.analyzers.lexer

import com.jalloft.compilador.analyzers.lexer.token.Lexical
import com.jalloft.compilador.errors.ErrorType

data class LexicalError(
    val type: ErrorType = ErrorType.LexicalError,
    val message: String,
    val line: Int
) : Lexical
