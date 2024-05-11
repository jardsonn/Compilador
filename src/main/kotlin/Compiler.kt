package com.jalloft.compilador

import com.google.common.io.Resources
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.Tokenizer
import com.jalloft.compilador.com.jalloft.compilador.utils.printToken

fun main() {
    val sourcePath = "fontes/codigo6.txt"
    val source = Resources.getResource(sourcePath).readText()
    val lexer = Tokenizer(source)

    var token = lexer.getNextToken()

    while (token != null) {
        printToken(token)
        token = lexer.getNextToken()
    }
}

