package com.jalloft.compilador

import com.google.common.io.Resources
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.Lexer
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.token.TokenBase

fun main() {
    val sourcePath = "fontes/codigo2.txt"
    val source = Resources.getResource(sourcePath).readText()
    val lexer = Lexer(source)

    var tokenBase = lexer.getNextToken()

    while (lexer.hasNext()) {
        val token = when (tokenBase) {
            is TokenBase.Token -> tokenBase
            is TokenBase.UnformedToken -> {
                println(tokenBase.errorMessage)
                return
            }
        }
        println("${token.lexeme}  ⇐------------------------------⇒   ${token.classification.description}")
        tokenBase = lexer.getNextToken()
    }
}