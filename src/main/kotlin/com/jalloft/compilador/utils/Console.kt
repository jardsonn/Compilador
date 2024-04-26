package com.jalloft.compilador.com.jalloft.compilador.utils

import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.token.Token
import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.LexicalError

fun printError(error: LexicalError) {
    val bold = "\u001b[1m"
    val redColor = "\u001b[31m"
    val reset = "\u001b[0m"
    println("${bold}${redColor}${error.type}: ${reset}${redColor}${error.message}${reset}")
    println("${bold}${redColor}Linha: ${reset}${redColor}${error.line()}${reset}")
}


fun printToken(token: Token) {
    val grayColor = "\u001b[37m"
    val reset = "\u001b[0m"
    val bgDarkGray = "\u001b[48;5;237m"

    val line = "${bgDarkGray}${grayColor} ${if (token.line() < 10) "0" + token.line() else token.line()} ".toBold()
    val lexeme = token.lexeme.toBold()
    val classz = token.classification.description.toBold()
    println("$line   $lexeme    $grayColor⇐⇒$reset      $classz")

}

fun String.toBold() = "\u001b[1m$this\u001b[0m"

