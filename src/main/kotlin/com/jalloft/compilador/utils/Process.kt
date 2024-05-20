package com.jalloft.compilador.utils

import com.jalloft.compilador.analyzers.lexer.LexicalError
import com.jalloft.compilador.analyzers.lexer.token.Token
import com.jalloft.compilador.analyzers.lexer.token.Tokens
import kotlin.system.exitProcess

fun exitProcessWithError(error: LexicalError){
    printError(error)
    exitProcess(0)
}

fun exitProcessWithError(tokens: Tokens, token: Token?){
    printError(tokens, token)
    exitProcess(0)
}

fun exitProcessWithError(errorMessage: String){
    printError(errorMessage)
    exitProcess(0)
}



fun exitProcess(){
    exitProcess(0)
}