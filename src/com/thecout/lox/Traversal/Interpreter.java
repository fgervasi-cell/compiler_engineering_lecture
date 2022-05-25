package com.thecout.lox.Traversal;


import com.thecout.lox.Parser.Expr.*;
import com.thecout.lox.Parser.Stmts.*;
import com.thecout.lox.Token;
import com.thecout.lox.TokenType;
import com.thecout.lox.Traversal.InterpreterUtils.Environment;
import com.thecout.lox.Traversal.InterpreterUtils.LoxCallable;
import com.thecout.lox.Traversal.InterpreterUtils.LoxFunction;
import com.thecout.lox.Traversal.InterpreterUtils.LoxReturn;
import com.thecout.lox.Traversal.InterpreterUtils.RuntimeError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Interpreter implements ExprVisitor<Object>,
        StmtVisitor<Void> {

    public final Environment globals = new Environment();
    private Environment environment = globals;




    public Interpreter() {
        globals.define("clock", new LoxCallable() {
            @Override
            public int arity() {
                return 0;
            }

            @Override
            public Object call(Interpreter interpreter,
                               List<Object> arguments) {
                return (double) System.currentTimeMillis() / 1000.0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }
        });
    }

    public void interpret(List<Stmt> statements) {
        try {
            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (RuntimeError error) {
            error.printStackTrace();
        }
    }

    public void executeBlock(List<Stmt> statements,
                             Environment environment) {
        Environment previous = this.environment;
        try {
            this.environment = environment;

            for (Stmt statement : statements) {
                execute(statement);
            }
        } catch (LoxReturn lr) {
        	throw lr;
        } finally {
            this.environment = previous;
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    public void execute(Stmt stmt) {
        stmt.accept(this);
    }


    @Override
    public Object visitAssignExpr(Assign expr) {
    	Object result = this.evaluate(expr.value);
        this.environment.assign(expr.name, result);
        return result;
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
    	Object left = this.evaluate(expr.left);
    	Object right = this.evaluate(expr.right);
    	if (left instanceof Literal) {
    		left = ((Literal) left).value;
    	}
    	if (right instanceof Literal) {
    		right = ((Literal) right).value;
    	}
    	switch (expr.operator.type) {
    	case GREATER:
    		return (double) left > (double) right;
    	case GREATER_EQUAL:
    		return (double) left >= (double) right;
    	case LESS:
    		return (double) left < (double) right;
    	case LESS_EQUAL:
    		return (double) left <= (double) right;
    	case PLUS:
    		return (double) left + (double) right;
    	case MINUS:
    		return (double) left - (double) right;
    	case STAR:
    		return (double) left * (double) right;
    	case SLASH:
    		if ((double) right == 0) {
    			throw new RuntimeError(expr.operator, "Cannot divide by zero.");
    		}
    		return (double) left / (double) right;
    	default:
    		break;
    	}
        return null;
    }

    @Override
    public Object visitCallExpr(Call expr) {
    	if (this.environment.get(((Variable) expr.callee).name) instanceof LoxFunction) {
    		LoxFunction fun = (LoxFunction) this.environment.get(((Variable) expr.callee).name);
        	return fun.call(this, expr.arguments.stream().map(Object.class::cast).collect(Collectors.toList()));
    	}
    	if (this.environment.get(((Variable) expr.callee).name) instanceof Interpreter) {
    		Interpreter intpt = (Interpreter) this.environment.get(((Variable) expr.callee).name);
    		return ((LoxCallable)intpt.globals.get(((Variable) expr.callee).name)).call(intpt, null);
    	}
    	return null;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return null;
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Logical expr) {
    	Object left = this.evaluate(expr.left);
    	Object right = this.evaluate(expr.right);
    	switch (expr.operator.type) {
    	case AND:
    		return (boolean) left && (boolean) right;
    	case OR:
    		return (boolean) left || (boolean) right;
    	default:
    		break;
    	}
        return null;
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
    	Object right = this.evaluate(expr.right);
    	switch (expr.operator.type) {
    	case MINUS:
    		return -(double) right;
    	case BANG:
    		return !(boolean) right;
    	default:
    		break;
    	}
        return null;
    }

    @Override
    public Object visitVariableExpr(Variable expr) {
        return this.environment.get(expr.name);
    }

    @Override
    public Void visitBlockStmt(Block stmt) {
    	for (Stmt statement: stmt.statements) {
    		statement.accept(this);
    	}
        return null;
    }

    @Override
    public Void visitExpressionStmt(Expression stmt) {
    	this.evaluate(stmt.expression);
        return null;
    }

    @Override
    public Void visitFunctionStmt(Function stmt) {
    	this.environment.define(stmt.name.lexeme, new LoxFunction(stmt, environment));
        return null;
    }

    @Override
    public Void visitIfStmt(If stmt) {
    	Object condition = this.evaluate(stmt.condition);
    	if ((boolean) condition) {
    		stmt.thenBranch.accept(this);
    	} else {
    		if (stmt.elseBranch != null) {
    			stmt.elseBranch.accept(this);
    		}
    	}
        return null;
    }

    @Override
    public Void visitPrintStmt(Print stmt) {
    	System.out.println(this.evaluate(stmt.expression));
        return null;
    }

    @Override
    public Void visitReturnStmt(Return stmt) {
    	throw new LoxReturn(this.evaluate(stmt.value));
    }

    @Override
    public Void visitVarStmt(Var stmt) {
    	this.environment.define(stmt.name.lexeme, this.evaluate(stmt.initializer));
		return null;
    }

    @Override
    public Void visitWhileStmt(While stmt) {
    	Object condition = this.evaluate(stmt.condition);
    	while((boolean) condition) {
    		stmt.body.accept(this);
    		condition = this.evaluate(stmt.condition);
    	}
        return null;
    }

}