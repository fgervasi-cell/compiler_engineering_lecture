package com.thecout.lox;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.thecout.lox.Parser.Parser;
import com.thecout.lox.Parser.Expr.Assign;
import com.thecout.lox.Parser.Expr.Call;
import com.thecout.lox.Parser.Expr.Literal;
import com.thecout.lox.Parser.Stmts.Block;
import com.thecout.lox.Parser.Stmts.Function;
import com.thecout.lox.Parser.Stmts.Print;
import com.thecout.lox.Parser.Stmts.Stmt;
import com.thecout.lox.Parser.Stmts.While;

public class ParserTest {
    static final String program = """
            fun printSum(a,b) {
            print a+b;
            }
            print 25+60;
            """;
    
    static final String program2 = """
    		while(true) {
    		print "true";
    		}
    		""";

    @Test
    void parseTest() {
        Scanner scanner = new Scanner(program);
        List<Token> actual = scanner.scan();
        Parser parser = new Parser(actual);
        List<Stmt> statements = parser.parse();
        assertTrue(statements.get(0) instanceof Function, "Expected Type Function got " + actual.get(0).getClass().getName());
        assertTrue(statements.get(1) instanceof Print, "Expected Type Print got " + actual.get(0).getClass().getName());
        assertTrue(((Function) statements.get(0)).body.get(0) instanceof Print, "Expected Type Print in function");
        assertEquals(((Function) statements.get(0)).parameters.get(0).type, TokenType.IDENTIFIER, "Expected first function parameter to be identifier");
    }
    
    @Test 
    void parsePrgrm2() {
    	Scanner scanner = new Scanner(program2);
    	List<Token> actual = scanner.scan();
    	Parser parser = new Parser(actual);
    	List<Stmt> statements = parser.parse();
    	assertTrue(statements.get(0) instanceof While, "Expected Type While got " + actual.get(0).getClass().getName());
    	While w = (While) statements.get(0);
    	assertTrue(w.body instanceof Block, "Expected Type Block as body");
    	assertTrue(((Block) w.body).statements.get(0) instanceof Print, "Expected Type Print in body");
    	Print p = (Print) ((Block) w.body).statements.get(0);
    	assertTrue(p.expression instanceof Assign, "Expected Assign got " + p.expression.getClass().getName());
    	assertTrue(w.condition instanceof Assign, "Expected Assign got " + w.condition.getClass().getName());
    	Assign cond = (Assign) w.condition;
    	assertTrue(((Call)cond.value).callee instanceof Literal, "Expected boolean literal 'true'");
    }
}
