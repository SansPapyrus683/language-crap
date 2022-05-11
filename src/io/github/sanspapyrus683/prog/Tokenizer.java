package io.github.sanspapyrus683.prog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Tokenizer {
    private static final Map<String, TokenType> keywords = Map.ofEntries(
            Map.entry("and", TokenType.AND),
            Map.entry("else", TokenType.ELSE),
            Map.entry("false", TokenType.FALSE),
            Map.entry("for", TokenType.FOR),
            Map.entry("if", TokenType.IF),
            Map.entry("null", TokenType.NULL),
            Map.entry("or", TokenType.OR),
            Map.entry("print", TokenType.PRINT),
            Map.entry("true", TokenType.TRUE),
            Map.entry("var", TokenType.VAR),
            Map.entry("while", TokenType.WHILE)
    );

    private final String src;
    private final List<Token> tokens = new ArrayList<>();
    private int prev = 0;
    private int at = 0;
    private int line = 1;

    public Tokenizer(String src) {
        this.src = src;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            prev = at;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case '%': addToken(TokenType.MOD); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            case '!':  addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
            case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;
            case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
            case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
            case '/':
                if (match('/')) {  // comment
                    for (; !isAtEnd() && peek() != '\n'; at++) ;
                } else {
                    addToken(TokenType.SLASH);
                }
                break;

            case '\n':  // whitespace crap
                line++;
            case ' ':
            case '\r':
            case '\t':
                break;

            case '"': handleString(); break;
            default:
                if (Character.isDigit(c)) {
                    handleNumber();
                } else if (isAlpha(c)) {
                    handleIdentifier();
                } else {
                    ActualLang.error(line, "wth is " + c);
                }
        }
    }

    //region handles the literals/identifiers
    private void handleString() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }
        if (isAtEnd()) {
            ActualLang.error(line, "you didn't complete the string...");
            return;
        }
        advance();  // that other " to close the string
        addToken(TokenType.STRING, src.substring(prev + 1, at - 1));
    }

    private void handleNumber() {
        for (; Character.isDigit(peek()); advance()) ;
        if (peek() == '.' && Character.isDigit(peekNext())) {
            advance();
            for (; Character.isDigit(peek()); advance()) ;
        }
        addToken(TokenType.NUMBER, Double.parseDouble(src.substring(prev, at)));
    }

    private void handleIdentifier() {
        for (; isAlphanumeric(peek()); advance());
        TokenType type = keywords.getOrDefault(src.substring(prev, at), TokenType.IDENTIFIER);
        addToken(type);
    }
    //endregion

    private boolean isAlpha(char c) {
        return Character.isAlphabetic(c) || c == '_';
    }

    private boolean isAlphanumeric(char c) {
        return Character.isDigit(c) || isAlpha(c);
    }

    //region checks & gets the characters or whatever
    private char advance() {
        return src.charAt(at++);
    }

    private boolean match(char c) {
        if (isAtEnd() || src.charAt(at) != c) {
            return false;
        }
        at++;
        return true;
    }

    private char peek() {
        return isAtEnd() ? '\0' : src.charAt(at);
    }

    private char peekNext() {
        return at + 1 >= src.length() ? '\0' : src.charAt(at + 1);
    }

    private boolean isAtEnd() {
        return at >= src.length();
    }
    //endregion

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String content = src.substring(prev, at);
        tokens.add(new Token(type, content, literal, line));
    }
}
