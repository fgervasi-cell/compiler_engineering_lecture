package com.thecout.lox;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class Scanner {
	private static final int WHITESPACE = 32;
	private static final int TABSPACE = 9;
	private String source;

	public Scanner(String source) {
		this.source = source;
	}

	public List<Token> scan() {
		List<Token> tokens = new ArrayList<>();
		String[] lines = source.split("\n");
		for (int i = 0; i < lines.length; i++) {
			tokens.addAll(scanLine(lines[i], i));
		}
		tokens.add(new Token(TokenType.EOF, "", "", lines.length));
		return tokens;
	}

	public List<Token> scanLine(String line, int lineNumber) {
		List<Token> tokens = new ArrayList<>();
		List<Character> memory = new ArrayList<>();
		Deque<Character> deque = new ArrayDeque<>();
		initializeStack(deque, line);
		while (!deque.isEmpty()) {
			memory.add(deque.pop());
			String possibleToken = buildFromMemory(memory);
			if (!possibleToken.isEmpty() && possibleToken.chars().allMatch(Character::isDigit)) {
				while (!deque.isEmpty() && (Character.isDigit(deque.peek()) || deque.peek() == '.')) {
					char c = deque.pop();
					if (Character.isDigit(c)) {
						memory.add(c);
					} else if (c == '.' && memory.stream().filter(ch -> ch == '.').collect(Collectors.toList()).isEmpty()) {
						memory.add(c);
					} else {
						tokens.add(new Token(TokenType.NUMBER, buildFromMemory(memory), buildFromMemory(memory), lineNumber));
						memory.clear();
						memory.add(c);
					}
				}
				possibleToken = buildFromMemory(memory);
				tokens.add(new Token(TokenType.NUMBER, possibleToken, Double.parseDouble(possibleToken), lineNumber));
				memory.clear();
			} else if (memory.stream().allMatch(c -> !Character.isDigit(c))) {
				if ("(".equals(possibleToken)) {
					tokens.add(new Token(TokenType.LEFT_PAREN, possibleToken, '(', lineNumber));
					memory.clear();
				} else if (")".equals(possibleToken)) {
					tokens.add(new Token(TokenType.RIGHT_PAREN, possibleToken, ')', lineNumber));
					memory.clear();
				} else if ("{".equals(possibleToken)) {
					tokens.add(new Token(TokenType.LEFT_BRACE, possibleToken, '{', lineNumber));
					memory.clear();
				} else if ("}".equals(possibleToken)) {
					tokens.add(new Token(TokenType.RIGHT_BRACE, possibleToken, '}', lineNumber));
					memory.clear();
				} else if ("\"".equals(possibleToken)) {
					while (!deque.isEmpty() && deque.peek() != '"') {
						memory.add(deque.pop());
					}
					memory.add(deque.pop());
					possibleToken = memory.stream().filter(c -> c != '"').map(String::valueOf)
												   .collect(Collectors.joining());
					tokens.add(new Token(TokenType.STRING, possibleToken, possibleToken.trim(), lineNumber));
					memory.clear();
				} else if (",".equals(possibleToken)) {
					tokens.add(new Token(TokenType.COMMA, possibleToken, ',', lineNumber));
					memory.clear();
				} else if (";".equals(possibleToken)) {
					tokens.add(new Token(TokenType.SEMICOLON, possibleToken, ';', lineNumber));
					memory.clear();
				} else if ("+".equals(possibleToken)) {
					tokens.add(new Token(TokenType.PLUS, possibleToken, '+', lineNumber));
					memory.clear();
				} else if ("-".equals(possibleToken)) {
					tokens.add(new Token(TokenType.MINUS, possibleToken, '-', lineNumber));
					memory.clear();
				} else if ("*".equals(possibleToken)) {
					tokens.add(new Token(TokenType.STAR, possibleToken, '*', lineNumber));
					memory.clear();
				} else if ("/".equals(possibleToken)) {
					tokens.add(new Token(TokenType.SLASH, possibleToken, '/', lineNumber));
					memory.clear();
				} else if (".".equals(possibleToken) && (deque.isEmpty() || !Character.isDigit(deque.peek()))) {
					tokens.add(new Token(TokenType.DOT, possibleToken, '.', lineNumber));
					memory.clear();
				} else if ("!".equals(possibleToken) && (deque.isEmpty() || deque.peek() != '=')) {
					tokens.add(new Token(TokenType.BANG, possibleToken, '!', lineNumber));
					memory.clear();
				} else if ("!=".equals(possibleToken)) {
					tokens.add(new Token(TokenType.BANG_EQUAL, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("=".equals(possibleToken) && (deque.isEmpty() || deque.peek() != '=')) {
					tokens.add(new Token(TokenType.EQUAL, possibleToken, '=', lineNumber));
					memory.clear();
				} else if ("==".equals(possibleToken)) {
					tokens.add(new Token(TokenType.EQUAL_EQUAL, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("<".equals(possibleToken) && (deque.isEmpty() || deque.peek() != '=')) {
					tokens.add(new Token(TokenType.LESS, possibleToken, '<', lineNumber));
					memory.clear();
				} else if ("<=".equals(possibleToken)) {
					tokens.add(new Token(TokenType.LESS_EQUAL, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if (">".equals(possibleToken) && (deque.isEmpty() || deque.peek() != '=')) {
					tokens.add(new Token(TokenType.GREATER, possibleToken, '>', lineNumber));
					memory.clear();
				} else if (">=".equals(possibleToken)) {
					tokens.add(new Token(TokenType.GREATER_EQUAL, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("print".equals(possibleToken) && (deque.isEmpty() || deque.peek() == WHITESPACE)) {
					tokens.add(new Token(TokenType.PRINT, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("if".equals(possibleToken) && (deque.isEmpty() || !Character.isLetter(deque.peek()))) {
					tokens.add(new Token(TokenType.IF, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("else".equals(possibleToken) && (deque.isEmpty() || !Character.isLetter(deque.peek()))) {
					tokens.add(new Token(TokenType.ELSE, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("for".equals(possibleToken) && (deque.isEmpty() || !Character.isLetter(deque.peek()))) {
					tokens.add(new Token(TokenType.FOR, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("while".equals(possibleToken) && (deque.isEmpty() || !Character.isLetter(deque.peek()))) {
					tokens.add(new Token(TokenType.WHILE, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("var".equals(possibleToken) && (deque.isEmpty() || !Character.isLetter(deque.peek()))) {
					tokens.add(new Token(TokenType.VAR, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("nil".equals(possibleToken) && (deque.isEmpty() || !Character.isLetter(deque.peek()))) {
					tokens.add(new Token(TokenType.NIL, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("or".equals(possibleToken) && (deque.isEmpty() || !Character.isLetter(deque.peek()))) {
					tokens.add(new Token(TokenType.OR, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("and".equals(possibleToken) && (deque.isEmpty() || !Character.isLetter(deque.peek()))) {
					tokens.add(new Token(TokenType.AND, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("return".equals(possibleToken) && (deque.isEmpty() || !Character.isLetter(deque.peek()))) {
					tokens.add(new Token(TokenType.RETURN, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("true".equals(possibleToken) && (deque.isEmpty() || !Character.isLetter(deque.peek()))) {
					tokens.add(new Token(TokenType.TRUE, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("false".equals(possibleToken) && (deque.isEmpty() || !Character.isLetter(deque.peek()))) {
					tokens.add(new Token(TokenType.FALSE, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if ("fun".equals(possibleToken) && (deque.isEmpty() || deque.peek() == WHITESPACE)) {
					tokens.add(new Token(TokenType.FUN, possibleToken, possibleToken, lineNumber));
					memory.clear();
				} else if (!possibleToken.isEmpty() && possibleToken.chars().allMatch(c -> Character.isDigit(c) || Character.isLetter(c)) 
						&& (deque.isEmpty() || !Character.isDigit(deque.peek()) && !Character.isLetter(deque.peek()))) {
					tokens.add(new Token(TokenType.IDENTIFIER, possibleToken, possibleToken, lineNumber));
					memory.clear();
				}
			}
		}
		return tokens;
	}

	private void initializeStack(Deque<Character> deque, String line) {
		char[] chars = line.toCharArray();
		for (int i = chars.length - 1; i >= 0; i--) {
			deque.push(chars[i]);
		}
	}

	private String buildFromMemory(List<Character> memory) {
		StringBuilder builder = new StringBuilder();
		for (char c : memory) {
			if (c != WHITESPACE && c != TABSPACE) {
				builder.append(c);
			}
		}
		return builder.toString();
	}
}
