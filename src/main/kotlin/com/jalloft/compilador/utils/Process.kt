package com.jalloft.compilador.com.jalloft.compilador.utils

import com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer.LexicalError
import kotlin.system.exitProcess

fun exitProcessWithError(error: LexicalError){
    printError(error)
    exitProcess(0)
}

fun exitProcess(){
    exitProcess(0)
}