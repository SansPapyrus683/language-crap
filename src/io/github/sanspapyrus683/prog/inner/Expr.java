package io.github.sanspapyrus683.prog.inner;

import io.github.sanspapyrus683.prog.Token;

public abstract class Expr {
    public interface Visitor<T> {
        T visitAssignmentExpr(Expr.Assignment expr);
        T visitBinaryExpr(Expr.Binary expr);
        T visitLiteralExpr(Expr.Literal expr);
        T visitGroupingExpr(Expr.Grouping expr);
        T visitLogicalExpr(Expr.Logical expr);
        T visitUnaryExpr(Expr.Unary expr);
        T visitVarExpr(Expr.Var expr);
    }

    public abstract <T> T accept(Visitor<T> visitor);

    public static class Assignment extends Expr {
        public final Token assignTo;
        public final Expr val;
        public Assignment(Token assignTo, Expr val) {
            this.assignTo = assignTo;
            this.val = val;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitAssignmentExpr(this);
        }
    }

    public static class Binary extends Expr {
        public final Expr left;
        public final Token op;
        public final Expr right;
        public Binary(Expr left, Token op, Expr right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    public static class Literal extends Expr {
        public final Object val;
        public Literal(Object val) {
            this.val = val;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    public static class Grouping extends Expr {
        public final Expr group;
        public Grouping(Expr group) {
            this.group = group;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    public static class Logical extends Expr {
        public final Expr left;
        public final Token op;
        public final Expr right;
        public Logical(Expr left, Token op, Expr right) {
            this.left = left;
            this.op = op;
            this.right = right;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }

    public static class Unary extends Expr {
        public final Token op;
        public final Expr right;
        public Unary(Token op, Expr right) {
            this.op = op;
            this.right = right;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    public static class Var extends Expr {
        public Token name;
        public Var(Token name) {
            this.name = name;
        }

        @Override
        public <T> T accept(Visitor<T> visitor) {
            return visitor.visitVarExpr(this);
        }
    }
}
