package io.github.sanspapyrus683.prog;

public class Token {
    public final TokenType type;
    public final String lexeme;  /** the actual content of the thing */
    public final Object literal;  /** the literal of the token (if it is one) */
    public final int line;  /** line number */

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString() {
        return "(" + type + " " + lexeme + " " + literal + ")";
    }
}
