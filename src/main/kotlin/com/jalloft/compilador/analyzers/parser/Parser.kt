package com.jalloft.compilador.analyzers.parser

import com.jalloft.compilador.analyzers.lexer.Tokenizer
import com.jalloft.compilador.analyzers.lexer.token.Token
import com.jalloft.compilador.analyzers.lexer.token.Tokens
import com.jalloft.compilador.utils.exitProcessWithError
import com.jalloft.compilador.utils.printSuccess

class Parser(private val sourcePath: String) {

    private val tokenizer by lazy { Tokenizer(sourcePath) }

    private var currentToken: Token? = null

    private fun advanceToNextToken() {
        currentToken = tokenizer.getNextToken()
    }

    fun parse() {
        advanceToNextToken()
        parseProgram()
        printSuccess()
    }

    private fun comsumeCurrentTokenAndAdvance(tokens: Tokens) {
        if (currentToken?.token != tokens) {
            exitProcessWithError(tokens)
        }
        advanceToNextToken()
    }

    private fun parseProgram() {
        comsumeCurrentTokenAndAdvance(Tokens.PRINCIPAL)
        comsumeCurrentTokenAndAdvance(Tokens.IDENTIFIER)
        comsumeCurrentTokenAndAdvance(Tokens.SEMICOLON)
        block()
    }

    private fun block() {
        if (currentToken?.token == Tokens.TYPE) {
            typeDefinition()
        }

        if (currentToken?.token == Tokens.VAR) {
            variablesDefinition()
        }

        while (currentToken?.token == Tokens.PROCEDURE || currentToken?.token == Tokens.FUNCTION) {
            subRoutinesDefinition()
        }

        compoundCommand()
    }


    private fun typeDefinition() {
        advanceToNextToken()
        comsumeCurrentTokenAndAdvance(Tokens.IDENTIFIER)
        comsumeCurrentTokenAndAdvance(Tokens.EQUAL)
        types()
        comsumeCurrentTokenAndAdvance(Tokens.SEMICOLON)
        while (currentToken?.token == Tokens.IDENTIFIER) {
            advanceToNextToken()
            comsumeCurrentTokenAndAdvance(Tokens.EQUAL)
            types()
        }
        comsumeCurrentTokenAndAdvance(Tokens.SEMICOLON)
    }

    private fun variablesDefinition() {
        advanceToNextToken()
        listIdentifiers()
//        se token ≠ ":" entao
        comsumeCurrentTokenAndAdvance(Tokens.COLON)
        types()
        comsumeCurrentTokenAndAdvance(Tokens.SEMICOLON)
//        enquanto token == "id" faca
        while (currentToken?.token == Tokens.IDENTIFIER) {
//            advanceToNextToken()
            listIdentifiers()
//            e token ≠ ":" entao
            comsumeCurrentTokenAndAdvance(Tokens.COLON)
            types()
        }
        advanceToNextToken()
    }

    private fun listIdentifiers() {
        comsumeCurrentTokenAndAdvance(Tokens.IDENTIFIER)
        // enquanto token == "," faca
        while (currentToken?.token == Tokens.COMMA) {
            advanceToNextToken()
            comsumeCurrentTokenAndAdvance(Tokens.IDENTIFIER)
        }
    }

    private fun subRoutinesDefinition() {
        if (currentToken?.token == Tokens.PROCEDURE) {
            procedureDefinition()
        } else if (currentToken?.token == Tokens.FUNCTION) {
            functionDefinition()
        } else {
            exitProcessWithError("'procedure' ou 'function' era esperado na linha ${currentToken?.line}")
        }
    }

    private fun functionDefinition() {
        advanceToNextToken()
        comsumeCurrentTokenAndAdvance(Tokens.IDENTIFIER)
        if (currentToken?.token == Tokens.LPAREN) {
            formalParameters()
        }
        comsumeCurrentTokenAndAdvance(Tokens.COLON)
        comsumeCurrentTokenAndAdvance(Tokens.IDENTIFIER)
        comsumeCurrentTokenAndAdvance(Tokens.SEMICOLON)
        block()
    }

    private fun procedureDefinition() {
        advanceToNextToken()
        comsumeCurrentTokenAndAdvance(Tokens.IDENTIFIER)
//        se token == "(" entao
        if (currentToken?.token == Tokens.LPAREN) {
            formalParameters()
        }
        comsumeCurrentTokenAndAdvance(Tokens.SEMICOLON)
        block()
    }

    private fun formalParameters() {
//        ( já foi consumido
        advanceToNextToken()
        listIdentifiers()
        comsumeCurrentTokenAndAdvance(Tokens.COLON)
        comsumeCurrentTokenAndAdvance(Tokens.IDENTIFIER)
        while (currentToken?.token == Tokens.SEMICOLON) {
            advanceToNextToken()
            listIdentifiers()
            comsumeCurrentTokenAndAdvance(Tokens.COLON)
            comsumeCurrentTokenAndAdvance(Tokens.IDENTIFIER)
        }
        comsumeCurrentTokenAndAdvance(Tokens.RPAREN)
    }

    private fun compoundCommand() {
        comsumeCurrentTokenAndAdvance(Tokens.BEGIN)
        unlabeledCommand() // Comando sem rótulo
        while (currentToken?.token == Tokens.SEMICOLON) {
            advanceToNextToken()
            unlabeledCommand()
        }
        comsumeCurrentTokenAndAdvance(Tokens.END)
    }

    //    12. <comando sem rotulo> ➝ <atribuicao>
//    |<chamada de procedimento>
//    |<comando condicional>
//    |<comando repetitivo>
//    13. <atribuicao> ➝ <variavel> := <expressao>
//    26. <variavel> ➝ <identificador>
//    19. <expressao> ➝ <expressao simples> [<relacao> <expressao simples>]
    private fun unlabeledCommand() {
        if (currentToken?.token == Tokens.IDENTIFIER) {
            advanceToNextToken()
            // se token == ":=" entao
            if (currentToken?.token == Tokens.ASSIGN) {
                assignment() // atribuicao
            } else if (currentToken?.token == Tokens.LPAREN) {
                procedureCall() // chamada de procedimento
            } else {
                exitProcessWithError(
                    "Esperado ':=' ou '(' após o identificador, porém foi encontrado '${currentToken?.token?.name?.uppercase()}' na linha ${
                        currentToken?.line?.minus(1)
                    }."
                )
            }
        } else if (currentToken?.token == Tokens.IF) {
            conditionalCommand() // comando condicional
        } else if (currentToken?.token == Tokens.WHILE) {
            repetitiveCommand() // comando repetitivo
        } else {
//            exitProcessWithError("Comando inválido")
        }
    }

    private fun repetitiveCommand() {
        advanceToNextToken()
        expression()
        comsumeCurrentTokenAndAdvance(Tokens.DO)
        unlabeledCommand()
    }

    private fun conditionalCommand() {
        advanceToNextToken()
        expression()
        comsumeCurrentTokenAndAdvance(Tokens.THEN)
        unlabeledCommand()
        if (currentToken?.token == Tokens.ELSE) {
            advanceToNextToken()
            unlabeledCommand()
        }
    }

    private fun procedureCall() {
        advanceToNextToken()
        listExpressions()
        comsumeCurrentTokenAndAdvance(Tokens.RPAREN)
    }

    private fun assignment() {
        advanceToNextToken()
        expression()
    }

    private fun expression() {
        simpleExpression()
        if (currentToken?.token == Tokens.EQUAL
            || currentToken?.token == Tokens.NOT_EQUAL
            || currentToken?.token == Tokens.LESS_THAN
            || currentToken?.token == Tokens.LESS_THAN_OR_EQUAL
            || currentToken?.token == Tokens.GREATER_THAN
            || currentToken?.token == Tokens.GREATER_THAN_OR_EQUAL
        ) {
            advanceToNextToken()
            simpleExpression()
        }
    }

    private fun simpleExpression() {
        if (currentToken?.token == Tokens.PLUS || currentToken?.token == Tokens.DASH) {
            advanceToNextToken()
        }
        term()
//        enquanto token == "+" ou token == "-" ou token == "or" faca
        while (currentToken?.token == Tokens.PLUS || currentToken?.token == Tokens.DASH || currentToken?.token == Tokens.OR) {
            advanceToNextToken()
            term()
        }
    }

    private fun term() {
        factor()
//        enquanto token == "*" ou token == "div" ou token == "and" faca
        while (currentToken?.token == Tokens.ASTERISK || currentToken?.token == Tokens.DIV || currentToken?.token == Tokens.AND) {
            advanceToNextToken()
            factor()
        }
    }

    private fun factor() {
        if (currentToken?.token == Tokens.IDENTIFIER) {
            advanceToNextToken()
            if (currentToken?.token == Tokens.LPAREN) {
                functionCall()
            }
        } else if (currentToken?.token == Tokens.NUMBER) {
            advanceToNextToken()
        } else if (currentToken?.token == Tokens.LPAREN) {
            advanceToNextToken()
            expression()
            comsumeCurrentTokenAndAdvance(Tokens.RPAREN)
        } else {
            exitProcessWithError("Fator inválido")
        }
    }

    private fun functionCall() {
        advanceToNextToken()
        listExpressions()
        comsumeCurrentTokenAndAdvance(Tokens.RPAREN)
    }

    private fun listExpressions() {
        expression()
        while (currentToken?.token == Tokens.COMMA) {
            advanceToNextToken()
            expression()
        }
    }


    private fun types() {
        if (currentToken?.token == Tokens.IDENTIFIER
            || currentToken?.token == Tokens.INT
            || currentToken?.token == Tokens.BOOLEAN
            || currentToken?.token == Tokens.DOUBLE
            || currentToken?.token == Tokens.CHAR
        ) {
            advanceToNextToken()
        } else {
            exitProcessWithError("Um tipo era esperado na linha ${currentToken?.line}")
        }

    }

    private fun exitProcessWithError(tokens: Tokens) {
        exitProcessWithError(tokens, currentToken)

    }

}