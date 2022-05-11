package io.github.sanspapyrus683.prog.inner;

import io.github.sanspapyrus683.prog.Token;
import io.github.sanspapyrus683.prog.errors.RuntimeError;

import java.util.HashMap;

public class Environment {
    private final HashMap<String, Object> vars = new HashMap<>();
    public void define(Token var, Object val) {
        vars.put(var.lexeme, val);
    }

    public void assign(Token var, Object val) {
        String name = var.lexeme;
        if (!vars.containsKey(name)) {
            throw new RuntimeError(var, "variable '" + name + "' not initialized you moron");
        }
        vars.put(name, val);
    }

    public Object get(Token var) {
        String name = var.lexeme;
        if (!vars.containsKey(name)) {
            throw new RuntimeError(var, "variable '" + name + "' not found");
        }
        return vars.get(name);
    }
}
