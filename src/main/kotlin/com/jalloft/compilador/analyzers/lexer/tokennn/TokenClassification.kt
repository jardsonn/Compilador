package com.jalloft.compilador.analyzers.lexer.tokennn


sealed class TokenClassification(override val name: String = "Símbolo especial") : TokenClass {
    data object Identifier : TokenClassification("Identificador")
    data object Digit : TokenClassification("Número")
    data object SpecialSymbol : TokenClassification()
    data object Keywords : TokenClassification("Palavra reservada")

    data object Principal : TokenClassification("principal"), Keyword
    data object If : TokenClassification("if"), Keyword
    data object Then : TokenClassification("then"), Keyword
    data object Else : TokenClassification("else"), Keyword
    data object While : TokenClassification("while"), Keyword
    data object Do : TokenClassification("do"), Keyword
    data object Until : TokenClassification("until"), Keyword
    data object Repeat : TokenClassification("repeat"), Keyword
    data object Int : TokenClassification("int"), Keyword
    data object Double : TokenClassification("double"), Keyword
    data object Char : TokenClassification("char"), Keyword
    data object Case : TokenClassification("case"), Keyword
    data object Switch : TokenClassification("switch"), Keyword
    data object End : TokenClassification("end"), Keyword
    data object Procedure : TokenClassification("procedure"), Keyword
    data object Function : TokenClassification("function"), Keyword
    data object For : TokenClassification("for"), Keyword
    data object Begin : TokenClassification("begin"), Keyword

    data object Semicolon : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol(";", "Ponto e vírgula")
    }

    data object Comma : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol(",", "Vírgula")
    }

    data object Period : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol(".", "Ponto")
    }

    data object Colon : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol(":", "Dois pontos")
    }

    data object At : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol("@", "Símbolo arroba")
    }

    data object LParenthesis : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol("(", "Parêntese esquerdo")
    }

    data object RParenthesis : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol(")", "Parêntese direito")
    }

    data object LBrace : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol("{", "Chave esquerda")
    }

    data object RBrace : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol("}", "Chave direita")
    }

    data object OpArithmeticPlus : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol("+", "Op. Aritmético de adição")
    }

    data object OpArithmeticMinus : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol("-", "Op. Aritmético de subtração")
    }

    data object OpArithmeticMultiplication : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol("*", "Op. Aritmético de multiplicação")
    }

    data object OpArithmeticDivide : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol("/", "Op. Aritmético de divisão")
    }

    data object OpLogicLessThan : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol("<", "Op. Lógico menor que")
    }

    data object OpLogicGreaterThan : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol(">", "Op. Lógico maior que")
    }

    data object OpEqual : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol("=", "Op. Lógico igual")
    }

    data object OpAssignment : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol(":=", "Op. atribuição")
    }

    data object OpLogicNotEqual : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol("<>", "Op. Lógico diferente")
    }

    data object OpLogicLessThanOrEqual : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol("<=", "Op. Lógico menor ou igual")
    }

    data object OpLogicGreaterThanOrEqual : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol(">=", "Op. Lógico maior ou igual")
    }

    data object OpMinusAssignment : TokenClassification(), SymbolEspecial {
        override val symbol get() = Symbol("-=", "Op. subtração e atribuição")
    }

    companion object {
        fun findSymbol(lexeme: String): TokenClassification? {
            return items[lexeme]
        }

        private val items = hashMapOf(
            ";" to Semicolon,
            "," to Comma,
            "." to Period,
            ":" to Colon,
            "@" to At,
            "(" to LParenthesis,
            ")" to RParenthesis,
            "{" to LBrace,
            "}" to RBrace,
            "+" to OpArithmeticPlus,
            "-" to OpArithmeticMinus,
            "*" to OpArithmeticMultiplication,
            "/" to OpArithmeticDivide,
            "<" to OpLogicLessThan,
            ">" to OpLogicGreaterThan,
            "=" to OpEqual,
            ":=" to OpAssignment,
            "<>" to OpLogicNotEqual,
            "<=" to OpLogicLessThanOrEqual,
            ">=" to OpLogicGreaterThanOrEqual,
            "-=" to OpMinusAssignment,
            "principal" to Principal,
            "if" to If,
            "then" to Then,
            "else" to Else,
            "while" to While,
            "do" to Do,
            "until" to Until,
            "repeat" to Repeat,
            "int" to Int,
            "double" to Double,
            "char" to Char,
            "case" to Case,
            "switch" to Switch,
            "end" to End,
            "procedure" to Procedure,
            "function" to Function,
            "for" to For,
            "begin" to Begin
        )

    }
}