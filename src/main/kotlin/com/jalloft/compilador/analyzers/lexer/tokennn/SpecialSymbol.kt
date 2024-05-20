package com.jalloft.compilador.analyzers.lexer.tokennn

sealed interface SymbolEspecial : TokenClass{
    val symbol: Symbol
}