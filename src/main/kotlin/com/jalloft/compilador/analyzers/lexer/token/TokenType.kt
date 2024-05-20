package com.jalloft.compilador.analyzers.lexer.token

enum class TokenType(val typeName: String) {
    RESERVED_WORD("Palavra Reservada"),
    SPECIAL_SYMBOL("Símbolo Especial"),
    NUMBER("Número"),
    IDENTIFIER("Identificador")
}