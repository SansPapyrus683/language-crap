package io.github.sanspapyrus683.prog.inner;

import io.github.sanspapyrus683.prog.ActualLang;
import io.github.sanspapyrus683.prog.errors.RuntimeError;
import io.github.sanspapyrus683.prog.Token;
import io.github.sanspapyrus683.prog.TokenType;

import java.util.List;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>  {
    private Environment environment = new Environment();

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt s : statements) {
                execute(s);
            }
        } catch (RuntimeError error) {
            ActualLang.runtimeError(error);
        }
    }

    //region statements
    private void execute(Stmt stmt) {
        stmt.accept(this);
    }

    @Override
    public Void visitBlock(Stmt.Block block) {
        for (Stmt s : block.content) {
            execute(s);
        }
        return null;  // times like these i despise java
    }

    @Override
    public Void visitExpr(Stmt.Expression expr) {
        evaluate(expr.expr);
        return null;
    }

    @Override
    public Void visitIf(Stmt.If stmt) {
        if (bool(evaluate(stmt.condition))) {
            execute(stmt.thenDo);
        } else if (stmt.elseDo != null) {
            execute(stmt.elseDo);
        }
        return null;
    }

    @Override
    public Void visitPrint(Stmt.Print expr) {
        System.out.println(evaluate(expr.expr));
        return null;
    }

    @Override
    public Void visitVar(Stmt.Var expr) {
        environment.define(expr.name, evaluate(expr.init));
        return null;
    }

    @Override
    public Void visitWhile(Stmt.While stmt) {
        while (bool(evaluate(stmt.condition))) {
            execute(stmt.toDo);
        }
        return null;
    }
    //endregion

    //region math expression stuff
    private Object evaluate(Expr obj) {
        return obj.accept(this);
    }

    @Override
    public Object visitAssignmentExpr(Expr.Assignment expr) {
        Object val = evaluate(expr.val);
        environment.assign(expr.assignTo, val);
        return val;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        switch (expr.op.type) {
            case PLUS:
                if (left instanceof String && right instanceof String) {
                    return left + (String) right;
                }
                if (left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                throw new RuntimeError(expr.op, "operands need to be the same");
            case MINUS:
                checkBothNums(expr.op, left, right);
                return (double) left - (double) right;
            case SLASH:
                checkBothNums(expr.op, left, right);
                return (double) left / (double) right;
            case STAR:
                checkBothNums(expr.op, left, right);
                return (double) left * (double) right;
            case MOD:
                checkBothNums(expr.op, left, right);
                return (double) left % (double) right;
            case GREATER:
                checkBothNums(expr.op, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkBothNums(expr.op, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkBothNums(expr.op, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkBothNums(expr.op, left, right);
                return (double) left <= (double) right;
            case EQUAL_EQUAL:
                return equal(left, right);
            case BANG_EQUAL:
                return !equal(left, right);
        }
        return null;  // what
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.val;
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.group);
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        boolean isOr = expr.op.type == TokenType.OR;
        Object left = evaluate(expr.left);
        if (isOr) {
            return bool(left) ? left : evaluate(expr.right);
        } else {
            return !bool(left) ? left : evaluate(expr.right);
        }
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object val = evaluate(expr.right);
        if (expr.op.type == TokenType.MINUS) {
            checkNum(expr.op, val);
            return -(double) val;
        } else if (expr.op.type == TokenType.BANG) {
            return !bool(val);
        }
        return null;  // what
    }

    @Override
    public Object visitVarExpr(Expr.Var expr) {
        return environment.get(expr.name);
    }
    //endregion

    private void checkNum(Token operand, Object toCheck) {
        if (toCheck instanceof Double) {
            return;
        }
        throw new RuntimeError(operand, "operand must be a number");
    }

    private void checkBothNums(Token operand, Object left, Object right) {
        if (!(left instanceof Double && right instanceof Double)) {
            throw new RuntimeError(operand, "operands must both be numbers");
        }
    }

    private boolean bool(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof String) {
            return ((String) obj).length() > 0;
        } else if (obj instanceof Double) {
            return ((double) obj) != 0;
        } else if (obj instanceof Boolean) {
            return (boolean) obj;
        }
        return true;  // what
    }

    private boolean equal(Object o1, Object o2) {
        if (o1 == null || o2 == null) {
            return o1 == o2;
        }
        return o1.equals(o2);
    }
}
