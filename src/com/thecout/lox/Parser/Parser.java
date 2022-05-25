package com.thecout.lox.Parser;


import static com.thecout.lox.TokenType.AND;
import static com.thecout.lox.TokenType.BANG;
import static com.thecout.lox.TokenType.BANG_EQUAL;
import static com.thecout.lox.TokenType.COMMA;
import static com.thecout.lox.TokenType.ELSE;
import static com.thecout.lox.TokenType.EOF;
import static com.thecout.lox.TokenType.EQUAL;
import static com.thecout.lox.TokenType.EQUAL_EQUAL;
import static com.thecout.lox.TokenType.FOR;
import static com.thecout.lox.TokenType.FUN;
import static com.thecout.lox.TokenType.GREATER;
import static com.thecout.lox.TokenType.GREATER_EQUAL;
import static com.thecout.lox.TokenType.IDENTIFIER;
import static com.thecout.lox.TokenType.IF;
import static com.thecout.lox.TokenType.LEFT_BRACE;
import static com.thecout.lox.TokenType.LEFT_PAREN;
import static com.thecout.lox.TokenType.LESS;
import static com.thecout.lox.TokenType.LESS_EQUAL;
import static com.thecout.lox.TokenType.MINUS;
import static com.thecout.lox.TokenType.OR;
import static com.thecout.lox.TokenType.PLUS;
import static com.thecout.lox.TokenType.PRINT;
import static com.thecout.lox.TokenType.RETURN;
import static com.thecout.lox.TokenType.RIGHT_BRACE;
import static com.thecout.lox.TokenType.RIGHT_PAREN;
import static com.thecout.lox.TokenType.SEMICOLON;
import static com.thecout.lox.TokenType.SLASH;
import static com.thecout.lox.TokenType.STAR;
import static com.thecout.lox.TokenType.VAR;
import static com.thecout.lox.TokenType.WHILE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.thecout.lox.Token;
import com.thecout.lox.TokenType;
import com.thecout.lox.Parser.Expr.Assign;
import com.thecout.lox.Parser.Expr.Binary;
import com.thecout.lox.Parser.Expr.Call;
import com.thecout.lox.Parser.Expr.Expr;
import com.thecout.lox.Parser.Expr.Literal;
import com.thecout.lox.Parser.Expr.Logical;
import com.thecout.lox.Parser.Expr.Unary;
import com.thecout.lox.Parser.Expr.Variable;
import com.thecout.lox.Parser.Stmts.Block;
import com.thecout.lox.Parser.Stmts.Expression;
import com.thecout.lox.Parser.Stmts.Function;
import com.thecout.lox.Parser.Stmts.If;
import com.thecout.lox.Parser.Stmts.Print;
import com.thecout.lox.Parser.Stmts.Return;
import com.thecout.lox.Parser.Stmts.Stmt;
import com.thecout.lox.Parser.Stmts.Var;
import com.thecout.lox.Parser.Stmts.While;

public class Parser {
    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private Expr expression() {
        return assignment();
    }

    private Stmt declaration() {
        try {
            if (match(FUN)) return function();
            if (match(VAR)) return varDeclaration();

            return statement();
        } catch (ParseError error) {
            return null;
        }
    }

    private Stmt statement() {
        if (match(FOR)) return forStatement();
        if (match(IF)) return ifStatement();
        if (match(PRINT)) return printStatement();
        if (match(RETURN)) return returnStatement();
        if (match(WHILE)) return whileStatement();
        if (match(LEFT_BRACE)) return new Block(block());

        return expressionStatement();
    }

    private Stmt forStatement() {
    	consume(LEFT_PAREN, "Expect '(' after 'for'.");
    	Stmt initializer = null;
    	Expr increment = null;
    	Expr condition = null;
    	if (match(VAR)) {
    		initializer = varDeclaration();
    	} else if (!match(SEMICOLON)) {
    		initializer = expressionStatement();
    	}
    	
    	if (!match(SEMICOLON)) {
    		condition = expression();
    		consume(SEMICOLON, "Expected ';'.");
    	}
    	increment = expression();
    	consume(RIGHT_PAREN, "Expect ')' after 'for' stmt.");
    	Stmt stmt = statement();
    	Block body = new Block(Arrays.asList(stmt, new Expression(increment)));
        return new Block(Arrays.asList(initializer, new While(condition, body)));
    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        Expr condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after if condition."); // [parens]

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new If(condition, thenBranch, elseBranch);
    }

    private Stmt printStatement() {
    	Expr expr = expression();
    	consume(SEMICOLON, "Expected ';' after print stmt.");
        return new Print(expr);
    }

    private Stmt returnStatement() {
    	if (match(SEMICOLON)) {
    		return new Return(null);
    	} 
    	Expr expr = expression();
    	consume(SEMICOLON, "Expected ';' at end of return stmt.");
        return new Return(expr);
    }

    private Stmt varDeclaration() {
    	Token id = consume(IDENTIFIER, "Expected identifier after variable keyword.");
    	Expr expr = null;
    	if (match(EQUAL)) {
    		expr = expression();
    	}
    	consume(SEMICOLON, "Expected ';' after variable declaration.");
        return new Var(id, expr);
    }

    private Stmt whileStatement() {
    	consume(LEFT_PAREN, "Expected '(' after 'while'.");
    	Expr expr = expression();
    	consume(RIGHT_PAREN, "Expected ')' after while condition.");
    	Stmt stmt = statement();
        return new While(expr, stmt);
    }

    private Stmt expressionStatement() {
    	Expr expr = expression();
    	consume(SEMICOLON, "Expected ';'.");
        return new Expression(expr);
    }

    private Function function() {
    	Token id = consume(IDENTIFIER, "Expected identifier after 'fun'.");
    	consume(LEFT_PAREN, "Expected '(' after identifier.");
    	List<Token> params = new ArrayList<>();
    	while(!match(RIGHT_PAREN)) {
    		params.add(consume(IDENTIFIER, "Expected identifier."));
    		match(COMMA);
    	}
    	consume(LEFT_BRACE, "Expected '{' before block.");
    	List<Stmt> block = block();
        return new Function(id, params, block);
    }

    private List<Stmt> block() {
    	List<Stmt> stmts = new ArrayList<>();
    	while(!match(RIGHT_BRACE)) {
        	stmts.add(declaration());
    	}
        return stmts;
    }

    private Expr assignment() {
    	Expr expr = or();
    	if (match(EQUAL)) {
    		Expr value = assignment();
    		if (expr instanceof Variable) {
    			Token name = ((Variable) expr).name;
    			return new Assign(name, value);
    		}
    	}
        return expr;
    }

    private Expr or() {
        Expr expr = and();

        while (match(OR)) {
            Token operator = previous();
            Expr right = and();
            expr = new Logical(expr, operator, right);
        }

        return expr;
    }

    private Expr and() {
    	Expr equ = equality();
    	while(match(AND)) {
    		Token operator = previous();
    		Expr right = equality();
    		equ = new Logical(equ, operator, right);
    	}
        return equ;
    }

    private Expr equality() {
    	Expr comp = comparison();
    	while(match(BANG_EQUAL) || match(EQUAL_EQUAL)) {
    		Token operator = previous();
    		Expr right = comparison();
    		comp = new Logical(comp, operator, right);
    	}
        return comp;
    }

    private Expr comparison() {
    	Expr add = addition();
    	while(match(GREATER) || match(GREATER_EQUAL) || match(LESS) || match(LESS_EQUAL)) {
    		Token operator = previous();
    		Expr right = addition();
    		add = new Binary(add, operator, right);
    	}
        return add;
    }

    private Expr addition() {
    	Expr mult = multiplication();
    	while (match(MINUS) || match(PLUS)) {
    		Token operator = previous();
    		Expr right = multiplication();
    		mult = new Binary(mult, operator, right);
    	}
        return mult;
    }

    private Expr multiplication() {
    	Expr unary = unary();
    	while (match(SLASH) || match(STAR)) {
    		Token operator = previous();
    		Expr right = unary();
    		unary = new Binary(unary, operator, right);
    	}
        return unary;
    }

    private Expr unary() {
    	if (match(BANG) || match(MINUS)) {
    		return new Unary(previous(), null);
    	}
        return call();
    }

    private Expr call() {
    	Expr expr = primary();
    	if (match(LEFT_PAREN)) {
    		List<Expr> exprs = new ArrayList<>();
    		if (this.peek().type != RIGHT_PAREN) {
    			exprs = arguments();
    		}
    		consume(RIGHT_PAREN, "Expected ')'.");
    		return new Call(expr, exprs);
    	}
    	return expr;
    }
    
    private List<Expr> arguments() {
    	List<Expr> exprs = new ArrayList<>();
    	exprs.add(expression());
    	while(match(COMMA)) {
    		exprs.add(expression());
    	}
    	return exprs;
    }

    private Expr primary() {
    	if (match(LEFT_PAREN)) {
    		Expr expr = expression();
    		consume(RIGHT_PAREN, "Expected ')'.");
    		return expr;
    	} else if (match(IDENTIFIER)) {
            return new Variable(previous());
    	} 
    	Token value = consume(tokens.get(current).type, "Parsing literal NUMBER | STRING | NIL | TRUE | FALSE.");
    	return new Literal(value.literal);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();

        throw error(peek(), message);
    }

    private boolean check(TokenType tokenType) {
        if (isAtEnd()) return false;
        return peek().type == tokenType;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        ParserError.error(token, message);
        return new ParseError();
    }


}
