package io.github.sanspapyrus683.prog;

public class Token {
    public final TokenType type;
    public final String lexeme;
    public final Object literal;
    public final int line;

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;  // the actual content of the thing
        this.literal = literal;  // if it's a literal, the content of the literal
        this.line = line;  // line number
    }

    @Override
    public String toString() {
        return "(" + type + " " + lexeme + " " + literal + ")";
    }
}
