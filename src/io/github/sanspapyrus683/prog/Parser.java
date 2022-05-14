package io.github.sanspapyrus683.prog;

import io.github.sanspapyrus683.prog.inner.Expr;
import io.github.sanspapyrus683.prog.inner.Stmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * you give this thing some tokens
 * it gives you an AST in return
 */
public class Parser {
    private static class ParseError extends RuntimeException { }

    private final List<Token> tokens;
    private int at = 0;  /** the current token we're at */

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> res = new ArrayList<>();
        while (peek().type != TokenType.EOF) {
            res.add(statement());
        }
        return res;
    }

    //region STATEMENT parsing
    private Stmt statement() {
        if (match(TokenType.PRINT)) {
            return printStatement();
        } else if (match(TokenType.VAR)) {
            return varStatement();
        } else if (match(TokenType.LEFT_BRACE)) {
            return blockStatement();
        } else if (match(TokenType.IF)) {
            return ifStatement();
        } else if (match(TokenType.WHILE)) {
            return whileStatement();
        } else if (match(TokenType.FOR)) {
            return forStatement();
        }
        return exprStatement();
    }

    private Stmt blockStatement() {
        List<Stmt> ret = new ArrayList<>();
        while (peek().type != TokenType.RIGHT_BRACE
                && peek().type != TokenType.EOF) {
            ret.add(statement());
        }
        consume(TokenType.RIGHT_BRACE, "you have to uh terminate w/ a left brace lol");
        return new Stmt.Block(ret);
    }

    private Stmt exprStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "you need to end w/ a semicolon my guy");
        return new Stmt.Expression(expr);
    }

    private Stmt forStatement() {
        consume(TokenType.LEFT_PAREN, "need a paren to start a for");
        Stmt init;
        if (match(TokenType.SEMICOLON)) {
            init = null;
        } else if (match(TokenType.VAR)) {
            init = varStatement();
        } else {
            init = exprStatement();
        }

        Expr condition = new Expr.Literal(true);
        if (peek().type != TokenType.SEMICOLON) {
            condition = expression();
        }
        consume(TokenType.SEMICOLON, "for condition needs to end w/ a ;");

        Expr post = null;
        if (peek().type != TokenType.RIGHT_PAREN) {
            post = expression();
        }
        consume(TokenType.RIGHT_PAREN, "for loop needs to end w/ a paren");

        Stmt toDo = statement();
        Stmt loopPart = toDo;
        if (post != null) {
            loopPart = new Stmt.Block(Arrays.asList(toDo, new Stmt.Expression(post)));
        }
        Stmt loop = new Stmt.While(condition, loopPart);
        if (init != null) {
            loop = new Stmt.Block(Arrays.asList(init, loop));
        }

        return loop;
    }

    private Stmt ifStatement() {
        consume(TokenType.LEFT_PAREN, "if condition needs to be in parens");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "if condition needs to be in parens");
        Stmt thenDo = statement();
        Stmt elseDo = match(TokenType.ELSE) ? statement() : null;
        return new Stmt.If(condition, thenDo, elseDo);
    }

    private Stmt printStatement() {
        Expr expr = expression();
        consume(TokenType.SEMICOLON, "you need to end w/ a semicolon my guy");
        return new Stmt.Print(expr);
    }

    private Stmt varStatement() {
        Token name = consume(TokenType.IDENTIFIER, "you need a variable name lol");
        consume(TokenType.EQUAL, "missing = after variable?");
        Expr val = expression();
        consume(TokenType.SEMICOLON, "you need to end w/ a semicolon my guy");
        return new Stmt.Var(name, val);
    }

    private Stmt whileStatement() {
        consume(TokenType.LEFT_PAREN, "need parens for the while condition fam");
        Expr condition = expression();
        consume(TokenType.RIGHT_PAREN, "need parens for the while condition fam");
        Stmt toDo = statement();
        return new Stmt.While(condition, toDo);
    }
    //endregion

    //region EXPRESSION parsing
    /** *basically* everything except the base literals */
    private Expr expression() {
        return assignment();
    }

    /** more REassignment than actual assignment (a = 1, not var a = 1) */
    private Expr assignment() {
        Expr expr = or();
        if (match(TokenType.EQUAL)) {
            Token equals = prev();
            if (expr instanceof Expr.Var) {
                Expr val = or();
                return new Expr.Assignment(((Expr.Var) expr).name, val);
            }
            throw error(equals, "bruh you have to assign to a variable");
        }
        return expr;
    }

    /** parses the || token (lower priority than and) */
    private Expr or() {
        Expr curr = and();
        if (match(TokenType.OR)) {
            Token op = prev();
            curr = new Expr.Logical(curr, op, and());
        }
        return curr;
    }

    /** parses the && stuff */
    private Expr and() {
        Expr curr = equality();
        if (match(TokenType.AND)) {
            Token op = prev();
            curr = new Expr.Logical(curr, op, equality());
        }
        return curr;
    }

    /** == or !=, that's it */
    private Expr equality() {
        Expr curr = comparison();  // consume the left side tokens
        while (match(TokenType.BANG_EQUAL, TokenType.EQUAL_EQUAL)) {
            Token op = prev();
            Expr right = comparison();
            curr = new Expr.Binary(curr, op, right);
        }
        return curr;
    }

    /** <=, <, you know all that stuff */
    private Expr comparison() {
        Expr curr = term();
        while (match(TokenType.LESS, TokenType.LESS_EQUAL,
                TokenType.GREATER, TokenType.GREATER_EQUAL)) {
            Token op = prev();
            Expr right = term();
            curr = new Expr.Binary(curr, op, right);
        }
        return curr;
    }

    /** addition/subtraction stuff */
    private Expr term() {
        Expr curr = factor();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token op = prev();
            Expr right = factor();
            curr = new Expr.Binary(curr, op, right);
        }
        return curr;
    }

    /** multiplication, division, & modulus */
    private Expr factor() {
        Expr curr = unary();
        while (match(TokenType.STAR, TokenType.SLASH, TokenType.MOD)) {
            Token op = prev();
            Expr right = factor();
            curr = new Expr.Binary(curr, op, right);
        }
        return curr;
    }

    /** like !bool or -bruh */
    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token op = prev();
            Expr right = unary();
            return new Expr.Unary(op, right);
        }
        return primary();
    }

    /** parses basically everything else (this is also where the recursion happens) */
    private Expr primary() {
        if (match(TokenType.FALSE)) {
            return new Expr.Literal(false);
        } else if (match(TokenType.TRUE)) {
            return new Expr.Literal(true);
        } else if (match(TokenType.NULL)) {
            return new Expr.Literal(null);
        } else if (match(TokenType.STRING, TokenType.NUMBER)) {
            return new Expr.Literal(prev().literal);
        } else if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "you need to complete your parentheses bruh");
            return new Expr.Grouping(expr);
        } else if (match(TokenType.IDENTIFIER)) {
            return new Expr.Var(prev());
        }
        throw error(peek(), "expected something");
    }
    //endregion

    /** checks if the current token is of the given type (errors if it doesn't lol) */
    private Token consume(TokenType type, String msg) {
        if (peek().type == type) {
            return advance();
        }
        throw error(peek(), msg);
    }

    private ParseError error(Token token, String msg) {
        ActualLang.error(token, msg);
        return new ParseError();
    }

    /**
     * if the next token matches one of them given, return true & consume it
     * if not, just return false & do nothing
     */
    private boolean match(TokenType... types) {
        for (TokenType t : types) {
            if (peek().type == t) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token peek() {
        return tokens.get(at);
    }

    private Token advance() {
        return tokens.get(at++);
    }

    private Token prev() {
        return tokens.get(at - 1);
    }
}
