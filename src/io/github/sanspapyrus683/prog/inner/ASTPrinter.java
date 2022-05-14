package io.github.sanspapyrus683.prog.inner;

/**
 * just a debugging class, not actually used in the problem
 * but yeah this prints out an AST in a somewhat human readable format
 */
public class ASTPrinter implements Expr.Visitor<String> {
    public String exprString(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitAssignmentExpr(Expr.Assignment expr) {
        return parenthesize(expr.assignTo.lexeme + " =", expr.val);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.op.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.group);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return expr.val == null ? "null" : expr.val.toString();
    }

    @Override
    public String visitLogicalExpr(Expr.Logical expr) {
        return parenthesize(expr.op.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.op.lexeme, expr.right);
    }

    @Override
    public String visitVarExpr(Expr.Var expr) {
        return parenthesize("var", expr);
    }

    // prints out a node of the AST
    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.accept(this));
        }
        builder.append(")");

        return builder.toString();
    }
}
