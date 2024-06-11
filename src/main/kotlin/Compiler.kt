package com.jalloft.compilador

import com.jalloft.compilador.analyzers.lexer.Tokenizer
import com.jalloft.compilador.analyzers.parser.Parser
import com.jalloft.compilador.utils.printToken

fun main() {
//    val sourcePath = "fontes/codigo_sintatico.txt"
    val sourcePath = "fontes/codigo1.txt"
    val parser = Parser(sourcePath)
    parser.parse()
}

