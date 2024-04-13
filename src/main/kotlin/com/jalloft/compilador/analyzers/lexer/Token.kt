package com.jalloft.compilador.com.jalloft.compilador.analyzers.lexer


data class Token(
    val classification: Tokens,
    val lexeme: String = "",
    val observation: String? = null,
    ) {
    override fun toString() = "$lexeme  ⇐------------------------------⇒   ${classification.description} ${(observation?.let { "($it)" }) ?: ""}"
}
