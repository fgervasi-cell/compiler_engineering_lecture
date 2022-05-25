package com.thecout.lox.Traversal;

import com.thecout.lox.Parser.Expr.*;
import com.thecout.lox.Parser.Stmts.*;

public class AstPrinter implements ExprVisitor<String>, StmtVisitor<String> {
    public String print(Expr expr) {
        return expr.accept(this);
    }

    public String print(Stmt stmt) {
        return stmt.accept(this);
    }

    // theoretisch kann/sollte hier verschiedenes verhalten implementiert werden
    
    @Override
    public String visitAssignExpr(Assign expr) {
        return expr.print();
    }

    @Override
    public String visitBinaryExpr(Binary expr) {
        return expr.print();
    }

    @Override
    public String visitCallExpr(Call expr) {
        return expr.print();
    }

    @Override
    public String visitGroupingExpr(Grouping expr) {
        return expr.print();
    }

    @Override
    public String visitLiteralExpr(Literal expr) {
        return expr.print();
    }

    @Override
    public String visitLogicalExpr(Logical expr) {
        return expr.print();
    }

    @Override
    public String visitUnaryExpr(Unary expr) {
        return expr.print();
    }

    @Override
    public String visitVariableExpr(Variable expr) {
        return expr.print();
    }

    @Override
    public String visitBlockStmt(Block stmt) {
        return stmt.print();
    }

    @Override
    public String visitExpressionStmt(Expression stmt) {
        return stmt.print();
    }

    @Override
    public String visitFunctionStmt(Function stmt) {
        return stmt.print();
    }

    @Override
    public String visitIfStmt(If stmt) {
        return stmt.print();
    }

    @Override
    public String visitPrintStmt(Print stmt) {
        return stmt.print();
    }

    @Override
    public String visitReturnStmt(Return stmt) {
        return stmt.print();
    }

    @Override
    public String visitVarStmt(Var stmt) {
        return stmt.print();
    }

    @Override
    public String visitWhileStmt(While stmt) {
        return stmt.print();
    }
}