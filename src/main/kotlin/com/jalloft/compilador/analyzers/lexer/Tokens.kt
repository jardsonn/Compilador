package com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer


enum class Tokens(val symbol: String? = null, val description: String? = null) {
    IDENTIFIER(description = "Identificador"),
    KEYWORD(description = "Palavra reservada"),
    INTEGER_NUMBER(description = "Número inteiro"),
    DECIMAL_NUMBER(description = "Número decimal"),
    SEMICOLON(";", "Ponto e vírgula"),
    SPECIAL_SYMBOL(description = "Símbolo especial"),
    COMMA(",", "Vírgula"),
    PERIOD(".", "Ponto"),
    COLON(":", "Dois pontos"),
    AT("@", "Símbolo arroba"),
    L_PARENTHESIS("(", "Parêntese esquerdo"),
    R_PARENTHESIS(")", "Parêntese direito"),
    L_BRACE("{", "Chave esquerda"),
    R_BRACE("}", "Chave direita"),
    OP_ARITHMETIC_PLUS("+", "Op. Aritmético de adição"),
    OP_ARITHMETIC_MINUS("-", "Op. Aritmético de subtração"),
    OP_ARITHMETIC_MULTIPLICATION("*", "Op. Aritmético de multiplicação"),
    OP_ARITHMETIC_DIVIDE("/", "Op. Aritmético de divisão"),
    OP_LOGIC_LESS_THAN("<", "Op. Lógico menor que"),
    OP_LOGIC_GREATER_THAN(">", "Op. Lógico maior que"),
    OP_EQUAL("=", "Op. Lógico igual"),
    OP_ASSIGNMENT(":=", "Op. atribuição"),
    OP_LOGIC_NOT_EQUAL("<>", "Op. Lógico diferente"),
    OP_LOGIC_LESS_THAN_OR_EQUAL("<=", "Op. Lógico menor ou igual"),
    OP_LOGIC_GREATER_THAN_OR_EQUAL(">=", "Op. Lógico maior ou igual"),
    OP_MINUS_ASSIGNMENT("-=", "Op. subtração e atribuição"),
}
