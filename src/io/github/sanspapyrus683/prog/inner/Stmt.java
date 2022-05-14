package io.github.sanspapyrus683.prog.inner;

import io.github.sanspapyrus683.prog.Token;

import java.util.List;

/**
 * statements are the things that actually DO stuff
 * like print statements, if statements, all that good stuff
 * they're handled the by interpreter
 */
public abstract class Stmt {
    public interface Visitor<T> {
        T visitBlock(Block stmt);
        T visitExpr(Expression stmt);
        T visitIf(If stmt);
        T visitPrint(Print stmt);
        T visitVar(Var stmt);
        T visitWhile(While stmt);
    }

    public abstract <T> T accept(Visitor<T> visitor);

    public static class Block extends Stmt {
        public final List<Stmt> content;
        public Block(List<Stmt> content) {
            this.content = content;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitBlock(this);
        }
    }

    public static class Expression extends Stmt {
        public final Expr expr;
        public Expression(Expr expr) {
            this.expr = expr;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitExpr(this);
        }
    }

    public static class If extends Stmt {
        public final Expr condition;
        public final Stmt thenDo;
        public final Stmt elseDo;
        public If(Expr condition, Stmt thenDo, Stmt elseDo) {
            this.condition = condition;
            this.thenDo = thenDo;
            this.elseDo = elseDo;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitIf(this);
        }
    }

    public static class Print extends Stmt {
        public final Expr expr;
        public Print(Expr expr) {
            this.expr = expr;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitPrint(this);
        }
    }

    public static class Var extends Stmt {
        public final Token name;
        public final Expr init;
        public Var(Token name, Expr init) {
            this.name = name;
            this.init = init;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitVar(this);
        }
    }

    public static class While extends Stmt {
        public final Expr condition;
        public final Stmt toDo;
        public While(Expr condition, Stmt toDo) {
            this.condition = condition;
            this.toDo = toDo;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitWhile(this);
        }
    }
}
