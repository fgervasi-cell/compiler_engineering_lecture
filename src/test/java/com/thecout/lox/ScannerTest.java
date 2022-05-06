package com.thecout.lox;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class ScannerTest {
	static final String program = """
			fun printSum(a,b) {
			print a+b;
			}
			print 25+60;
			""";

	static final String program2 = """
			var x;
			x = 10;
			var y = 1.5;
			print x * y;
			""";

	static final String program3 = """
			if (a <= b) {
			return true;
			} else {
			return false;
			}
			""";

	static final String program4 = """
			while (true) {
			print "true";
			}
			for (var i = 0; i < 50; i = i+1) {
			print i;
			}
			""";
	
	static final String program5 = """
			if ((true and false) or (true and a)) { }
			""";

	@Test
	void scanLineTest() {
		Scanner scanner = new Scanner(program);
		List<Token> actual = scanner.scanLine(program.split("\n")[0], 0);
		List<TokenType> expected = Arrays.asList(TokenType.FUN, TokenType.IDENTIFIER, TokenType.LEFT_PAREN,
				TokenType.IDENTIFIER, TokenType.COMMA, TokenType.IDENTIFIER, TokenType.RIGHT_PAREN,
				TokenType.LEFT_BRACE);
		for (int i = 0; i < actual.size(); i++) {
			assertEquals(expected.get(i), actual.get(i).type,
					"Expected " + expected.get(i) + ", got " + actual.get(i).type);
		}
		assertEquals(expected.size(), actual.size(), "Expected " + expected.size() + " tokens, got " + actual.size());
	}

	@Test
	void scanTest() {
		Scanner scanner = new Scanner(program);
		List<Token> actual = scanner.scan();
		assertEquals(20, actual.size(), "Expected 20 tokens, got " + actual.size());
	}

	@Test
	void scanTest2() {
		Scanner scanner = new Scanner(program2);
		List<Token> actual = scanner.scan();
		List<TokenType> expected = Arrays.asList(TokenType.VAR, TokenType.IDENTIFIER, TokenType.SEMICOLON,
				TokenType.IDENTIFIER, TokenType.EQUAL, TokenType.NUMBER, TokenType.SEMICOLON, TokenType.VAR,
				TokenType.IDENTIFIER, TokenType.EQUAL, TokenType.NUMBER, TokenType.SEMICOLON, TokenType.PRINT,
				TokenType.IDENTIFIER, TokenType.STAR, TokenType.IDENTIFIER, TokenType.SEMICOLON, TokenType.EOF);
		assertEquals(18, actual.size(), "Expected 18 tokens, got " + actual.size());
		for (int i = 0; i < actual.size(); i++) {
			assertEquals(expected.get(i), actual.get(i).type,
					"Expected " + expected.get(i) + ", got " + actual.get(i).type);
		}
	}

	@Test
	void scanTest3() {
		Scanner scanner = new Scanner(program3);
		List<Token> actual = scanner.scan();
		List<TokenType> expected = Arrays.asList(TokenType.IF, TokenType.LEFT_PAREN, TokenType.IDENTIFIER,
				TokenType.LESS_EQUAL, TokenType.IDENTIFIER, TokenType.RIGHT_PAREN, TokenType.LEFT_BRACE,
				TokenType.RETURN, TokenType.TRUE, TokenType.SEMICOLON, TokenType.RIGHT_BRACE, TokenType.ELSE,
				TokenType.LEFT_BRACE, TokenType.RETURN, TokenType.FALSE, TokenType.SEMICOLON, TokenType.RIGHT_BRACE,
				TokenType.EOF);
		for (int i = 0; i < actual.size(); i++) {
			assertEquals(expected.get(i), actual.get(i).type,
					"Expected " + expected.get(i) + ", got " + actual.get(i).type);
		}
	}

	@Test
	void scanTest4() {
		Scanner scanner = new Scanner(program4);
		List<Token> actual = scanner.scan();
		List<TokenType> expected = Arrays.asList(TokenType.WHILE, TokenType.LEFT_PAREN, TokenType.TRUE,
				TokenType.RIGHT_PAREN, TokenType.LEFT_BRACE, TokenType.PRINT, TokenType.STRING, TokenType.SEMICOLON,
				TokenType.RIGHT_BRACE, TokenType.FOR, TokenType.LEFT_PAREN, TokenType.VAR, TokenType.IDENTIFIER,
				TokenType.EQUAL, TokenType.NUMBER, TokenType.SEMICOLON, TokenType.IDENTIFIER, TokenType.LESS,
				TokenType.NUMBER, TokenType.SEMICOLON, TokenType.IDENTIFIER, TokenType.EQUAL, TokenType.IDENTIFIER,
				TokenType.PLUS, TokenType.NUMBER, TokenType.RIGHT_PAREN, TokenType.LEFT_BRACE, TokenType.PRINT,
				TokenType.IDENTIFIER, TokenType.SEMICOLON, TokenType.RIGHT_BRACE, TokenType.EOF);
		for (int i = 0; i < actual.size(); i++) {
			assertEquals(expected.get(i), actual.get(i).type,
					"Expected " + expected.get(i) + ", got " + actual.get(i).type);
		}
	}
	
	@Test
	void scanTest5() {
		Scanner scanner = new Scanner(program5);
		List<Token> actual = scanner.scan();
		List<TokenType> expected = Arrays.asList(TokenType.IF, TokenType.LEFT_PAREN, TokenType.LEFT_PAREN,
				TokenType.TRUE, TokenType.AND, TokenType.FALSE, TokenType.RIGHT_PAREN, TokenType.OR,
				TokenType.LEFT_PAREN, TokenType.TRUE, TokenType.AND, TokenType.IDENTIFIER, 
				TokenType.RIGHT_PAREN, TokenType.RIGHT_PAREN, TokenType.LEFT_BRACE, TokenType.RIGHT_BRACE,
				TokenType.EOF);
		for (int i = 0; i < actual.size(); i++) {
			assertEquals(expected.get(i), actual.get(i).type,
					"Ecpected " + expected.get(i) + ", got " + actual.get(i).type);
		}
	}

	@Test
	void scanNumber() {
		Scanner scanner = new Scanner("12.45");
		List<Token> actual = scanner.scan();
		assertEquals(2, actual.size(), "Expected 2 token, got " + actual.size());
		assertEquals(TokenType.NUMBER, actual.get(0).type,
				"Expected " + TokenType.NUMBER + ", got " + actual.get(0).type);
		assertEquals(12.45, actual.get(0).literal, "Expected 12.45, got " + actual.get(0).literal);
	}

	@Test
	void scanString() {
		Scanner scanner = new Scanner("print \"Hello World\";");
		List<Token> actual = scanner.scan();
		assertEquals(4, actual.size(), "Expected 4 token, got " + actual.size());
		assertEquals(TokenType.STRING, actual.get(1).type,
				"Expected " + TokenType.STRING + ", got " + actual.get(1).type);
		assertEquals("Hello World", actual.get(1).literal, "Expected Hello World got " + actual.get(1).literal);
	}
}

