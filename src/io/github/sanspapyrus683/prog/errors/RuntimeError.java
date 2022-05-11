package io.github.sanspapyrus683.prog.errors;

import io.github.sanspapyrus683.prog.Token;

public class RuntimeError extends RuntimeException {
    public final Token token;
    public RuntimeError(Token token, String msg) {
        super(msg);
        this.token = token;
    }
}
