package com.jalloft.compilador.analyzers.lexer.token


enum class Tokens(val type: TokenType, val content: String? = null) {
    NUMBER(TokenType.NUMBER),
    IDENTIFIER(TokenType.IDENTIFIER),

    PRINCIPAL(TokenType.RESERVED_WORD, "principal"),
    IF(TokenType.RESERVED_WORD, "if"),
    THEN(TokenType.RESERVED_WORD, "then"),
    ELSE(TokenType.RESERVED_WORD, "else"),
    WHILE(TokenType.RESERVED_WORD, "while"),
    DO(TokenType.RESERVED_WORD, "do"),
    UNTIL(TokenType.RESERVED_WORD, "until"),
    REPEAT(TokenType.RESERVED_WORD, "repeat"),
    INT(TokenType.RESERVED_WORD, "int"),
    DOUBLE(TokenType.RESERVED_WORD, "double"),
    CHAR(TokenType.RESERVED_WORD, "char"),
    BOOLEAN(TokenType.RESERVED_WORD, "boolean"),
    CASE(TokenType.RESERVED_WORD, "case"),
    SWITCH(TokenType.RESERVED_WORD, "switch"),
    END(TokenType.RESERVED_WORD, "end"),
    PROCEDURE(TokenType.RESERVED_WORD, "procedure"),
    FUNCTION(TokenType.RESERVED_WORD, "function"),
    FOR(TokenType.RESERVED_WORD, "for"),
    BEGIN(TokenType.RESERVED_WORD, "begin"),
    TYPE(TokenType.RESERVED_WORD, "type"),
    VAR(TokenType.RESERVED_WORD, "var"),

    OR(TokenType.RESERVED_WORD, "or"),
    DIV(TokenType.RESERVED_WORD, "div"),
    AND(TokenType.RESERVED_WORD, "and"),

    SEMICOLON(TokenType.SPECIAL_SYMBOL, ";"),
    COMMA(TokenType.SPECIAL_SYMBOL, ","),
    PERIOD(TokenType.SPECIAL_SYMBOL, "."),
    PLUS(TokenType.SPECIAL_SYMBOL, "+"),
    DASH(TokenType.SPECIAL_SYMBOL, "-"),
    ASTERISK(TokenType.SPECIAL_SYMBOL, "*"),
    SLASH(TokenType.SPECIAL_SYMBOL, "/"),
    LPAREN(TokenType.SPECIAL_SYMBOL, "("),
    RPAREN(TokenType.SPECIAL_SYMBOL, ")"),
    LESS_THAN(TokenType.SPECIAL_SYMBOL, "<"),
    GREATER_THAN(TokenType.SPECIAL_SYMBOL, ">"),
    COLON(TokenType.SPECIAL_SYMBOL, ":"),
    EQUAL(TokenType.SPECIAL_SYMBOL, "="),
    LBRACE(TokenType.SPECIAL_SYMBOL, "{"),
    RBRACE(TokenType.SPECIAL_SYMBOL, "}"),
    ASSIGN(TokenType.SPECIAL_SYMBOL, ":="),
    NOT_EQUAL(TokenType.SPECIAL_SYMBOL, "<>"),
    LESS_THAN_OR_EQUAL(TokenType.SPECIAL_SYMBOL, "<="),
    GREATER_THAN_OR_EQUAL(TokenType.SPECIAL_SYMBOL, ">="),
    AT(TokenType.SPECIAL_SYMBOL, "@"),
    MINUS_ASSIGN(TokenType.SPECIAL_SYMBOL, "-=");

    override fun toString(): String {
        return super.toString().uppercase()
    }
    
}
