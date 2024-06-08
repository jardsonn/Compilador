package com.jalloft.compilador

import com.jalloft.compilador.analyzers.lexer.Tokenizer
import com.jalloft.compilador.analyzers.parser.Parser
import com.jalloft.compilador.utils.printToken

fun main() {
    val sourcePath = "fontes/codigo1.txt"
    val parser = Parser(sourcePath)
    parser.parse()
//    val tokenizer = Tokenizer(sourcePath)
//    var token = tokenizer.getNextToken()
//    while (token != null){
//        printToken(token)
//        token = tokenizer.getNextToken()
//    }
}

