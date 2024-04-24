package com.jalloft.compilador

import com.google.common.io.Resources
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.Lexer
import com.jalloft.compilador.com.jalloft.compilador.utils.printToken

fun main() {
    val sourcePath = "fontes/codigo2.txt"
    val source = Resources.getResource(sourcePath).readText()
    val lexer = Lexer(source)

    var token = lexer.getNextToken()

    while (token != null) {
        printToken(token)
        token = lexer.getNextToken()
    }
}

